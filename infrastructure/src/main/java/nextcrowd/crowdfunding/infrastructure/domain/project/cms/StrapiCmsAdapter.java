package nextcrowd.crowdfunding.infrastructure.domain.project.cms;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.ContentRef;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.ContentRefResponse;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.CreateProjectOwnerRequest;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.CreateProjectRequest;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.CreateRewardRequest;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.ProjectListResponse;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.ProjectOwnerData;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.ProjectOwnerListResponse;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.ProjectResponse;
import nextcrowd.crowdfunding.infrastructure.domain.project.cms.model.UploadedFile;
import nextcrowd.crowdfunding.infrastructure.storage.FileStorageService;
import nextcrowd.crowdfunding.infrastructure.storage.StorageUtils;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.model.CreateProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.model.UploadedResource;
import nextcrowd.crowdfunding.project.model.UploadedResourceId;
import nextcrowd.crowdfunding.project.port.CmsPort;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StrapiCmsAdapter implements CmsPort {

    private static final Logger logger = LoggerFactory.getLogger(StrapiCmsAdapter.class);
    private final FileStorageService fileStorageService;
    private final String baseUrl;
    private final String token;
    private final OkHttpClient strapiOkHttpClient;
    private final ObjectMapper strapiObjectMapper;
    private final String strapiPublicUrl;

    public StrapiCmsAdapter(
            FileStorageService fileStorageService,
            @Value("${strapi.public-url}") String strapiPublicUrl,
            @Value("${strapi.base-url}") String baseUrl,
            @Value("${strapi.token}") String token) {
        this.fileStorageService = fileStorageService;
        this.baseUrl = baseUrl;
        this.token = token;
        this.strapiPublicUrl = strapiPublicUrl;
        this.strapiOkHttpClient = new OkHttpClient().newBuilder()
                                                    .connectTimeout(10, TimeUnit.SECONDS)
                                                    .callTimeout(10, TimeUnit.SECONDS)
                                                    .readTimeout(10, TimeUnit.SECONDS)
                                                    .writeTimeout(10, TimeUnit.SECONDS)
                                                    .build();
        this.strapiObjectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        ;
        strapiObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    @Override
    public void saveContent(CreateProjectContent command) {

        CompletableFuture<String> saveOrCreateProjectOwnerFuture = CompletableFuture.supplyAsync(() -> getOwnerOrCreate(command.getOwner()));
        CompletableFuture<Optional<String>> saveImageFuture = CompletableFuture.supplyAsync(() -> saveResourceOnCms(
                command.getImage(),
                "image_project_%s".formatted(command.getProjectId()
                                                    .id())));
        CompletableFuture<Optional<String>> saveVideoIdFuture = CompletableFuture.supplyAsync(() -> saveResourceOnCms(
                command.getVideo(),
                "video_project_%s".formatted(command.getProjectId()
                                                    .id())));

        CompletableFuture<List<String>> saveRewardsFuture = CompletableFuture.supplyAsync(() -> command.getRewards()
                                                                                                       .stream()
                                                                                                       .map(this::saveReward)
                                                                                                       .toList());
        try {
            CompletableFuture.allOf(saveOrCreateProjectOwnerFuture, saveImageFuture, saveVideoIdFuture, saveRewardsFuture).join();


            String ownerId = saveOrCreateProjectOwnerFuture.get();
            Optional<String> maybeImageId = saveImageFuture.get();
            Optional<String> maybeVideoId = saveVideoIdFuture.get();
            List<String> rewardIds = saveRewardsFuture.get();
            String projectId = saveProject(ownerId, maybeImageId.orElse(null), maybeVideoId.orElse(null), rewardIds, command);
            logger.info("Project saved with id: {}", projectId);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to save project", e);
            throw new RuntimeException(e);
        }
    }

    private @NotNull Optional<String> saveResourceOnCms(UploadedResource resource, String fileName) {
        if (resource == null) {
            return Optional.empty();
        }
        if (resource.getLocation() == UploadedResource.Location.CMS) {
            return Optional.of(resource.getId().id());
        }

        return Optional.ofNullable(resource.getId())
                       .map(UploadedResourceId::id)
                       .map(StorageUtils.StorageId::parse)
                       .flatMap(fileStorageService::load)
                       .map(storageResource -> storeFile(
                               storageResource.content(),
                               storageResource.contentType(),
                               fileName));
    }

    private String saveProject(String ownerId, @Nullable String imageId, @Nullable String videoId, List<String> rewardIds, CreateProjectContent command) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(this.baseUrl + "/api/crowdfunding-projects"))
                                 .newBuilder()
                                 .build();

        CreateProjectRequest createProjectRequest = CreateProjectRequest.builder()
                                                                        .data(CreateProjectRequest.Project.builder()
                                                                                                          .projectId(command.getProjectId().id())
                                                                                                          .title(command.getTitle())
                                                                                                          .longDescription(command.getLongDescription())
                                                                                                          .description(command.getDescription())
                                                                                                          .projectOwner(new ContentRef(ownerId))
                                                                                                          .image(new ContentRef(imageId))
                                                                                                          .video(new ContentRef(videoId))
                                                                                                          .currency(command.getCurrency())
                                                                                                          .requestedAmount(command.getRequestedAmount()
                                                                                                                                  .doubleValue())
                                                                                                          .startDate(convertLocalDateToISO(command.getProjectStartDate()))
                                                                                                          .endDate(convertLocalDateToISO(command.getProjectEndDate()))
                                                                                                          .rewards(rewardIds
                                                                                                                           .stream()
                                                                                                                           .map(ContentRef::new)
                                                                                                                           .toList())
                                                                                                          .build())
                                                                        .build();
        try {
            String requestBodyJson = strapiObjectMapper.writeValueAsString(createProjectRequest);
            Request request = new Request.Builder()
                    .url(httpUrl.url())
                    .post(RequestBody.create(requestBodyJson, okhttp3.MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer %s".formatted(this.token))
                    .build();
            return strapiObjectMapper.readValue(executeRequest(request), ProjectResponse.class).getId();
        } catch (Exception e) {
            logger.error("something went wrong while saving project", e);
            throw new RuntimeException(e);
        }
    }

    private static String convertLocalDateToISO(Instant date) {
        return date.atZone(ZoneId.of("UTC")).toLocalDate().toString();
    }

    private String saveReward(SubmitCrowdfundingProjectCommand.ProjectReward projectReward) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(this.baseUrl + "/api/rewards"))
                                 .newBuilder()
                                 .build();
        ContentRef rewardImage = Optional.ofNullable(projectReward.getImage())
                                         .flatMap(resource -> this.saveResourceOnCms(
                                                 resource,
                                                 "reward_"
                                                 + projectReward.getName()))
                                         .map(ContentRef::new)
                                         .orElse(null);
        CreateRewardRequest createRewardRequest = CreateRewardRequest.builder()
                                                                     .data(CreateRewardRequest.Reward.builder()
                                                                                                     .description(projectReward.getDescription())
                                                                                                     .name(projectReward.getName())
                                                                                                     .image(rewardImage)
                                                                                                     .build())
                                                                     .build();
        try {


            String responseJson = strapiObjectMapper.writeValueAsString(createRewardRequest);
            Request request = new Request.Builder()
                    .url(httpUrl.url())
                    .post(RequestBody.create(responseJson, okhttp3.MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer %s".formatted(this.token))
                    .build();
            return strapiObjectMapper.readValue(executeRequest(request), ContentRefResponse.class).getData().getId();
        } catch (Exception e) {
            logger.error("something went wrong while saving reward", e);
            throw new RuntimeException(e);
        }

    }

    private String getOwnerOrCreate(SubmitCrowdfundingProjectCommand.ProjectOwner owner) {
        logger.debug("getting or creating owner {}", owner);
        Optional<ProjectOwnerData> maybeProjectOwner = getProjectOwner(owner);


        return maybeProjectOwner
                .map(ProjectOwnerData::getId)
                .orElseGet(() -> {
                    logger.debug("owner not found, creating owner {}", owner);
                    Optional<String> maybeImageId = saveResourceOnCms(owner.getImage(), "owner_%s".formatted(owner.getName()));
                    return saveProjectOwner(owner, maybeImageId.orElse(null))
                            .getId();

                });

    }

    private ContentRef saveProjectOwner(SubmitCrowdfundingProjectCommand.ProjectOwner owner, String imageId) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(this.baseUrl + "/api/project-owners"))
                                 .newBuilder()
                                 .build();
        CreateProjectOwnerRequest createRewardRequest = CreateProjectOwnerRequest.builder()
                                                                                 .data(CreateProjectOwnerRequest.ProjectOwner.builder()
                                                                                                                             .name(owner.getName())
                                                                                                                             .image(Optional.ofNullable(imageId)
                                                                                                                                            .map(ContentRef::new)
                                                                                                                                            .orElse(null))
                                                                                                                             .build())

                                                                                 .build();
        try {
            Request request = new Request.Builder()
                    .url(httpUrl.url())
                    .post(RequestBody.create(strapiObjectMapper.writeValueAsString(createRewardRequest), okhttp3.MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer %s".formatted(this.token))
                    .build();
            return strapiObjectMapper.readValue(executeRequest(request), ContentRefResponse.class).getData();
        } catch (Exception e) {
            logger.error("something went wrong while saving owner", e);
            throw new RuntimeException(e);
        }
    }

    private @NotNull Optional<ProjectOwnerData> getProjectOwner(SubmitCrowdfundingProjectCommand.ProjectOwner owner) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(this.baseUrl + "/api/project-owners"))
                                 .newBuilder()
                                 .addQueryParameter("filters[name]", owner.getName())
                                 .build();

        Request request = new Request.Builder()
                .url(httpUrl.url())
                .get()
                .addHeader("Authorization", "Bearer %s".formatted(this.token))
                .build();

        return executeRequestOptional(request)
                .map(responseBody -> convertJson(responseBody, ProjectOwnerListResponse.class))
                .map(ProjectOwnerListResponse::getData)
                .map(List::stream)
                .flatMap(Stream::findFirst)
                ;
    }

    private <T> T convertJson(String responseBody, Class<T> cls) {
        try {
            return strapiObjectMapper.readValue(responseBody, cls);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert json", e);
            throw new RuntimeException(e);
        }
    }

    private Optional<String> executeRequestOptional(Request request) {
        try (
                Response response = strapiOkHttpClient.newCall(request).execute()
        ) {
            Optional<String> maybeResponseBodyStr = getResponseBody(response);
            if (response.code() == 400) {
                return Optional.empty();
            }
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get owner. Response code: %s and body is %s".formatted(response.code(), maybeResponseBodyStr.orElse("")));
            }
            return maybeResponseBodyStr;
        } catch (IOException e) {
            logger.error("Failed to execute request", e);
            throw new RuntimeException(e);
        }
    }

    private String executeRequest(Request request) {
        try (
                Response response = strapiOkHttpClient.newCall(request).execute()
        ) {
            Optional<String> maybeResponseBodyStr = getResponseBody(response);
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to execute request. Response code: %s and body is %s".formatted(
                        response.code(),
                        maybeResponseBodyStr.orElse("")));
            }
            logger.info("the request was {} response body {}", request.url(), maybeResponseBodyStr.orElseThrow());
            return maybeResponseBodyStr.orElseThrow(() -> new IllegalStateException("expected a json body in response"));
        } catch (IOException e) {
            logger.error("Failed to execute request", e);
            throw new RuntimeException(e);
        }
    }


    private static Optional<String> getResponseBody(Response response) {
        return Optional.of(response)
                       .map(Response::body)
                       .map(body -> {
                           try {
                               return body.string();
                           } catch (IOException e) {
                               logger.error("Failed to get response body", e);
                               throw new RuntimeException(e);
                           }
                       });
    }

    private String storeFile(byte[] content, String contentType, String fileName) {


        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "files",
                        fileName,
                        RequestBody.create(content, MediaType.parse(contentType)) // Adjust media type as needed
                )
                .build();
        Request request = new Request.Builder()
                .url(this.baseUrl + "/api/upload")
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        String responseBody = executeRequest(request);
        logger.info("response body of upload {}", responseBody);
        return convertJson(responseBody, ContentRef[].class)[0].getId();

    }

    @Override
    public Optional<ProjectContent> getProjectContent(ProjectId projectId) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(this.baseUrl + "/api/crowdfunding-projects"))
                                 .newBuilder()
                                 .addQueryParameter("filters[projectId]", projectId.id())
                                 .addQueryParameter("populate[0]", "timeline")
                                 .addQueryParameter("populate[1]", "timeline.timelineEvent")
                                 .addQueryParameter("populate[2]", "rewards")
                                 .addQueryParameter("populate[3]", "image")
                                 .addQueryParameter("populate[4]", "video")
                                 .addQueryParameter("populate[5]", "videoShortVersion")
                                 .addQueryParameter("populate[6]", "projectOwner")
                                 .addQueryParameter("populate[7]", "projectOwner.image")
                                 .addQueryParameter("populate[8]", "rewards.image")
                                 .build();

        Request request = new Request.Builder()
                .url(httpUrl.url())
                .get()
                .addHeader("Authorization", "Bearer %s".formatted(this.token))
                .build();

        return executeRequestOptional(request)
                .map(responseBody -> convertJson(responseBody, ProjectListResponse.class))
                .map(ProjectListResponse::getData)
                .map(List::stream)
                .flatMap(Stream::findFirst)
                .map(projectResponse -> ProjectContent.builder()
                                                      .video(Optional.ofNullable(projectResponse.getVideo())
                                                                     .map(this::convertFromCmsToUploadedResource)
                                                                     .orElse(null))
                                                      .image(Optional.ofNullable(projectResponse.getImage())
                                                                     .map(this::convertFromCmsToUploadedResource)
                                                                     .orElse(null))
                                                      .projectStartDate(convertToInstant(projectResponse.getStartDate()))
                                                      .projectEndDate(convertToInstant(projectResponse.getEndDate()))
                                                      .minimumInvestment(Optional.ofNullable(projectResponse.getMinimumInvestment())
                                                                                 .map(BigDecimal::valueOf)
                                                                                 .orElse(null))
                                                      .expectedProfit(Optional.ofNullable(projectResponse.getExpectedProfit())
                                                                              .map(BigDecimal::valueOf)
                                                                              .orElse(null))
                                                      .risk(projectResponse.getRisk())
                                                      .numberOfBackers(projectResponse.getNumberOfBackers())
                                                      .collectedAmount(Optional.ofNullable(projectResponse.getCollectedAmount())
                                                                               .map(BigDecimal::valueOf)
                                                                               .orElse(null))
                                                      .requestedAmount(Optional.ofNullable(projectResponse.getRequestedAmount())
                                                                               .map(BigDecimal::valueOf)
                                                                               .orElse(null))
                                                      .title(projectResponse.getTitle())
                                                      .description(projectResponse.getDescription())
                                                      .longDescription(projectResponse.getLongDescription())
                                                      .rewards(projectResponse.getRewards().stream().map(rewardData -> ProjectReward.builder()
                                                                                                                                    .description(rewardData.getDescription())
                                                                                                                                    .name(rewardData.getName())
                                                                                                                                    .image(Optional.ofNullable(
                                                                                                                                                           rewardData.getImage())
                                                                                                                                                   .map(this::convertFromCmsToUploadedResource)
                                                                                                                                                   .orElse(null))
                                                                                                                                    .build()).toList())
                                                      .projectId(projectId)
                                                      .owner(Optional.ofNullable(projectResponse.getProjectOwner())
                                                                     .map(owner -> ProjectContent.ProjectOwner.builder()
                                                                                                              .name(owner.getName())
                                                                                                              .id(owner.getId())
                                                                                                              .image(Optional.ofNullable(owner.getImage())
                                                                                                                             .map(this::convertFromCmsToUploadedResource)
                                                                                                                             .orElse(null))
                                                                                                              .build())
                                                                     .orElse(null)
                                                      )
                                                      .currency(projectResponse.getCurrency())
                                                      .build());
    }

    private UploadedResource convertFromCmsToUploadedResource(UploadedFile uploadedFile) {
        return UploadedResource.builder()
                               .path(uploadedFile.getUrl())
                               .location(UploadedResource.Location.CMS)
                               .url(this.strapiPublicUrl + uploadedFile.getUrl())
                               .contentType(uploadedFile.getMime())
                               .id(new UploadedResourceId(uploadedFile.getId()))
                               .build();
    }

    private static Instant convertToInstant(String date) {
        return LocalDate.parse(date).atStartOfDay().toInstant(ZoneOffset.UTC);
    }


}
