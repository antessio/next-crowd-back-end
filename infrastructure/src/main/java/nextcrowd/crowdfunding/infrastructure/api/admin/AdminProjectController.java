package nextcrowd.crowdfunding.infrastructure.api.admin;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartFile;

import nextcrowd.crowdfunding.admin.api.AdminApi;
import nextcrowd.crowdfunding.admin.api.model.AddInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.admin.api.model.CancelInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.CrowdfundingProject;
import nextcrowd.crowdfunding.admin.api.model.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.admin.api.model.FileUploadResponse;
import nextcrowd.crowdfunding.admin.api.model.Investment;
import nextcrowd.crowdfunding.admin.api.model.PaginatedInvestmentsResponse;
import nextcrowd.crowdfunding.admin.api.model.PaginatedProjectsResponse;
import nextcrowd.crowdfunding.admin.api.model.ProjectCreated;
import nextcrowd.crowdfunding.admin.api.model.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.infrastructure.api.ApiError;
import nextcrowd.crowdfunding.infrastructure.api.admin.adapter.ApiConverter;
import nextcrowd.crowdfunding.infrastructure.storage.FileStorageService;
import nextcrowd.crowdfunding.project.ProjectServicePort;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Controller
public class AdminProjectController implements AdminApi {

    private final ProjectServicePort projectServicePort;
    private final FileStorageService fileStorageService;

    public AdminProjectController(ProjectServicePort projectServicePort, FileStorageService fileStorageService) {
        this.projectServicePort = projectServicePort;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ResponseEntity<ProjectCreated> adminProjectsPost(SubmitCrowdfundingProjectCommand submitCrowdfundingProjectCommand) {
        return Optional.of(submitCrowdfundingProjectCommand)
                       .map(ApiConverter::toDomain)
                       .map(projectServicePort::submitProject)
                       .map(nextcrowd.crowdfunding.project.model.ProjectId::id)
                       .map(id -> new ProjectCreated().id(id))
                       .map(ResponseEntity::ok)
                       .orElseThrow();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdEditPut(String projectId, EditCrowdfundingProjectCommand editCrowdfundingProjectCommand) {
        nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand domainCommand = Optional.of(editCrowdfundingProjectCommand)
                                                                                                      .map(ApiConverter::toDomain)
                                                                                                      .orElseThrow();
        projectServicePort.editProject(new nextcrowd.crowdfunding.project.model.ProjectId(projectId), domainCommand);
        return ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdApprovePost(String projectId, ApproveCrowdfundingProjectCommand approveCrowdfundingProjectCommand) {
        projectServicePort.approve(
                new nextcrowd.crowdfunding.project.model.ProjectId(projectId),
                ApiConverter.toDomain(approveCrowdfundingProjectCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdInvestmentsCancelPost(
            String projectId,
            CancelInvestmentCommand cancelInvestmentCommand) {
        projectServicePort.cancelInvestment(new nextcrowd.crowdfunding.project.model.ProjectId(projectId), ApiConverter.toDomain(cancelInvestmentCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdInvestmentsConfirmPost(
            String projectId,
            ConfirmInvestmentCommand confirmInvestmentCommand) {
        projectServicePort.confirmInvestment(
                new nextcrowd.crowdfunding.project.model.ProjectId(projectId),
                ApiConverter.toDomain(confirmInvestmentCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdInvestmentsPost(String projectId, AddInvestmentCommand addInvestmentCommand) {
        projectServicePort.addInvestment(new nextcrowd.crowdfunding.project.model.ProjectId(projectId), ApiConverter.toDomain(addInvestmentCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdIssuePost(String projectId) {
        projectServicePort.issue(new nextcrowd.crowdfunding.project.model.ProjectId(projectId));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdRejectPost(String projectId) {
        projectServicePort.reject(new nextcrowd.crowdfunding.project.model.ProjectId(projectId));
        return ResponseEntity.accepted().build();
    }


    @Override
    public ResponseEntity<PaginatedProjectsResponse> adminProjectsPendingReviewGet(String cursor, Integer limit) {
        List<nextcrowd.crowdfunding.project.model.CrowdfundingProject> projects = new ArrayList<>(projectServicePort.getPendingReviewProjects(new ProjectId(
                                                                                                                            cursor))
                                                                                                                    .limit(limit + 1)
                                                                                                                    .toList());
        return convertToPaginatedListWithContent(limit, projects);
    }

    private @NotNull ResponseEntity<PaginatedProjectsResponse> convertToPaginatedListWithContent(
            Integer limit,
            List<nextcrowd.crowdfunding.project.model.CrowdfundingProject> projects) {
        boolean hasMore = projects.size() > limit;
        if (hasMore) {
            projects.removeLast();
        }
        Map<ProjectId, ProjectContent> projectIdProjectContentMap = projects
                .stream()
                .map(nextcrowd.crowdfunding.project.model.CrowdfundingProject::getId)
                .map(projectServicePort::getContentById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(ProjectContent::getProjectId, pc -> pc));

        List<CrowdfundingProject> results = new ArrayList<>(projects
                                                                    .stream()
                                                                    .map(p -> ApiConverter.toApi(p, projectIdProjectContentMap.get(p.getId())))
                                                                    .toList());
        return ResponseEntity.ok(new PaginatedProjectsResponse()
                                         .data(results)
                                         .hasMore(hasMore));
    }

    @Override
    public ResponseEntity<CrowdfundingProject> adminProjectsProjectIdGet(String projectId) {
        return projectServicePort.getById(new nextcrowd.crowdfunding.project.model.ProjectId(projectId))
                                 .map(p -> ApiConverter.toApi(p, projectServicePort.getContentById(p.getId()).orElse(null)))
                                 .map(ResponseEntity::ok)
                                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaginatedInvestmentsResponse> adminProjectsProjectIdInvestmentsAcceptedGet(String projectId, String cursor, Integer limit) {
        InvestmentId startingFrom = Optional.ofNullable(cursor)
                                            .map(InvestmentId::new)
                                            .orElse(null);
        List<Investment> results = projectServicePort.getAcceptedInvestments(
                                                             new nextcrowd.crowdfunding.project.model.ProjectId(
                                                                     projectId), startingFrom)
                                                     .limit(limit + 1)
                                                     .map(ApiConverter::toApi)
                                                     .toList();
        boolean hasMore = results.size() > limit;
        if (hasMore) {
            results.removeLast();
        }
        return ResponseEntity.ok(new PaginatedInvestmentsResponse()
                                         .data(results)
                                         .hasMore(hasMore));
    }

    @Override
    public ResponseEntity<PaginatedInvestmentsResponse> adminProjectsProjectIdInvestmentsPendingGet(String projectId, String cursor, Integer limit) {
        InvestmentId startingFrom = Optional.ofNullable(cursor)
                                            .map(InvestmentId::new)
                                            .orElse(null);
        List<Investment> results = new ArrayList<>(projectServicePort.getPendingInvestments(
                                                                             new nextcrowd.crowdfunding.project.model.ProjectId(
                                                                                     projectId), startingFrom)
                                                                     .limit(limit + 1)
                                                                     .map(ApiConverter::toApi)
                                                                     .toList());
        boolean hasMore = results.size() > limit;
        if (hasMore) {
            results.removeLast();
        }
        return ResponseEntity.ok(new PaginatedInvestmentsResponse()
                                         .data(results)
                                         .hasMore(hasMore));
    }

    @Override
    public ResponseEntity<PaginatedProjectsResponse> adminProjectsPublishedGet(String cursor, Integer limit) {
        List<nextcrowd.crowdfunding.project.model.CrowdfundingProject> projects = new ArrayList<>(projectServicePort.getPublishedProjects(new ProjectId(
                                                                                                                                 cursor))
                                                                                                                         .limit(limit + 1)
                                                                                                                         .toList());
        return convertToPaginatedListWithContent(limit, projects);
    }

    @Override
    public ResponseEntity<FileUploadResponse> adminUploadPost(MultipartFile file) {
        return Optional.ofNullable(file)
                       .filter(Predicate.not(MultipartFile::isEmpty))
                       .map(this::getStoreFile)
                       .map(uri -> new FileUploadResponse().url(uri))
                       .map(ResponseEntity::ok)
                       .orElseThrow(() -> new ValidationException("File is empty"));

    }

    private URI getStoreFile(MultipartFile multipartFile) {
        return fileStorageService.storeFile(getMultipartFileFunction(multipartFile), multipartFile.getContentType());
    }

    private static byte[] getMultipartFileFunction(MultipartFile f) {
        try {
            return f.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(CrowdfundingProjectException.class)
    public ResponseEntity<ApiError> handleCrowdfundingProjectException(CrowdfundingProjectException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

}
