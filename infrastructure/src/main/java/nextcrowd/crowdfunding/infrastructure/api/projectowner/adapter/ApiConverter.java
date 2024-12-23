package nextcrowd.crowdfunding.infrastructure.api.projectowner.adapter;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.model.UploadedResource;
import nextcrowd.crowdfunding.project.model.UploadedResourceId;

public final class ApiConverter {

    public static final ZoneOffset UTC = ZoneOffset.UTC;

    private ApiConverter() {
    }

    public static SubmitCrowdfundingProjectCommand toDomain(
            nextcrowd.crowdfunding.projectowner.api.model.SubmitCrowdfundingProjectCommand submitCrowdfundingProjectCommand,
            ProjectOwner projectOwner) {
        return SubmitCrowdfundingProjectCommand.builder()
                                               .title(submitCrowdfundingProjectCommand.getTitle())
                                               .image(convertUploadResourceFromApi(submitCrowdfundingProjectCommand.getImage()))
                                               .requestedAmount(submitCrowdfundingProjectCommand.getRequestedAmount())
                                               .currency(submitCrowdfundingProjectCommand.getCurrency())
                                               .projectStartDate(submitCrowdfundingProjectCommand.getProjectStartDate().toInstant())
                                               .projectEndDate(submitCrowdfundingProjectCommand.getProjectEndDate().toInstant())
                                               .description(submitCrowdfundingProjectCommand.getDescription())
                                               .longDescription(submitCrowdfundingProjectCommand.getLongDescription())
                                               .video(convertUploadResourceFromApi(submitCrowdfundingProjectCommand.getProjectVideo()))
                                               .owner(Optional.ofNullable(projectOwner)
                                                              .map(po -> SubmitCrowdfundingProjectCommand.ProjectOwner.builder()
                                                                                                                      .id(po.getId().id())
                                                                                                                      .name(po.getName())
                                                                                                                      .build())
                                                              .orElse(null))
                                               .rewards(Optional.ofNullable(submitCrowdfundingProjectCommand.getRewards()).orElseGet(List::of)
                                                                .stream()
                                                                .map(ApiConverter::convertProjectReward)
                                                                .toList())
                                               .build();

    }

    private static String convertResourceUrl(String imageUrl) {
        return Optional.ofNullable(imageUrl)
                       .filter(Predicate.not(String::isBlank))
                       .orElse(null);
    }

    private static @Nullable UploadedResource convertUploadResourceFromApi(nextcrowd.crowdfunding.projectowner.api.model.UploadedResource resource) {
        return Optional.ofNullable(resource)
                       .map(r -> UploadedResource.builder()
                                                 .path(r.getPath())
                                                 .contentType(r.getContentType())
                                                 .location(UploadedResource.Location.valueOf(r.getLocation()))
                                                 .url(r.getUrl().toString())
                                                 .id(new UploadedResourceId(r.getId()))
                                                 .build())
                       .orElse(null);
    }

    private static @Nullable nextcrowd.crowdfunding.projectowner.api.model.UploadedResource convertUploadResourceToApi(UploadedResource resource) {
        return Optional.ofNullable(resource)
                       .map(r -> {
                           nextcrowd.crowdfunding.projectowner.api.model.UploadedResource uploadedResource
                                   = new nextcrowd.crowdfunding.projectowner.api.model.UploadedResource();
                           uploadedResource.path(r.getPath());
                           uploadedResource.contentType(r.getContentType());
                           uploadedResource.location(r.getLocation().name());
                           uploadedResource.url(URI.create(r.getUrl()));
                           uploadedResource.id(r.getId().id());
                           return uploadedResource;
                       })
                       .orElse(null);
    }

    private static SubmitCrowdfundingProjectCommand.ProjectReward convertProjectReward(nextcrowd.crowdfunding.projectowner.api.model.ProjectReward projectReward) {
        return SubmitCrowdfundingProjectCommand.ProjectReward.builder()
                                                             .name(projectReward.getName())
                                                             .description(projectReward.getDescription())
                                                             .image(convertUploadResourceFromApi(projectReward.getImage()))
                                                             .build();
    }

    public static nextcrowd.crowdfunding.projectowner.api.model.CrowdfundingProject toApi(CrowdfundingProject project, ProjectContent projectContent) {
        return new nextcrowd.crowdfunding.projectowner.api.model.CrowdfundingProject()
                .id(project.getId().id())
                .status(project.getStatus().name())
                .requestedAmount(project.getRequestedAmount().doubleValue())
                .collectedAmount(project.getCollectedAmount().map(BigDecimal::doubleValue).orElse(null))
                .currency(project.getCurrency())
                .projectStartDate(convertToOffsetDateTime(project.getProjectStartDate()))
                .projectEndDate(convertToOffsetDateTime(project.getProjectEndDate()))
                .minimumInvestment(project.getMinimumInvestment().map(BigDecimal::doubleValue).orElse(null))
                .expectedProfit(project.getExpectedProfit().map(BigDecimal::doubleValue).orElse(null))
                .risk(project.getRisk().orElse(null))
                .owner(projectOwnerToApi(project.getOwner(), Optional.ofNullable(projectContent).map(ProjectContent::getOwner).orElse(null)))
                .numberOfBackers(project.getNumberOfBackers().orElse(null))
                .title(Optional.ofNullable(projectContent).map(ProjectContent::getTitle).orElse(null))
                .description(Optional.ofNullable(projectContent).map(ProjectContent::getDescription).orElse(null))
                .longDescription(Optional.ofNullable(projectContent).map(ProjectContent::getLongDescription).orElse(null))
                .image(Optional.ofNullable(projectContent).map(ProjectContent::getImage).map(ApiConverter::convertUploadResourceToApi).orElse(null))
                .projectVideo(Optional.ofNullable(projectContent).map(ProjectContent::getVideo).map(ApiConverter::convertUploadResourceToApi).orElse(null))
                .rewards(Optional.ofNullable(projectContent).map(ProjectContent::getRewards).orElseGet(List::of)
                                 .stream()
                                 .map(ApiConverter::projectRewardToApi)
                                 .toList())
                ;

    }

    private static nextcrowd.crowdfunding.projectowner.api.model.ProjectReward projectRewardToApi(ProjectReward projectReward) {
        return new nextcrowd.crowdfunding.projectowner.api.model.ProjectReward()
                .name(projectReward.getName())
                .description(projectReward.getDescription())
                .image(convertUploadResourceToApi(projectReward.getImage()));
    }

    private static nextcrowd.crowdfunding.projectowner.api.model.ProjectOwner projectOwnerToApi(ProjectOwner owner, ProjectContent.ProjectOwner projectOwnerContent) {
        return new nextcrowd.crowdfunding.projectowner.api.model.ProjectOwner()
                .id(owner.getId().id())
                .name(owner.getName())
                .image(Optional.ofNullable(projectOwnerContent).map(ProjectContent.ProjectOwner::getImage).map(ApiConverter::convertUploadResourceToApi).orElse(null));
    }


    public static OffsetDateTime convertToOffsetDateTime(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, UTC);
    }

}
