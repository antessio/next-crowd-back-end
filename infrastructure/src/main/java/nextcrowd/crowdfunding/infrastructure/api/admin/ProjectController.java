package nextcrowd.crowdfunding.infrastructure.api.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nextcrowd.crowdfunding.admin.api.AdminApi;
import nextcrowd.crowdfunding.admin.api.model.AddInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.admin.api.model.CancelInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.CrowdfundingProject;
import nextcrowd.crowdfunding.admin.api.model.Investment;
import nextcrowd.crowdfunding.admin.api.model.PaginatedInvestmentsResponse;
import nextcrowd.crowdfunding.admin.api.model.PaginatedProjectsResponse;
import nextcrowd.crowdfunding.admin.api.model.ProjectId;
import nextcrowd.crowdfunding.admin.api.model.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.infrastructure.api.ApiError;
import nextcrowd.crowdfunding.infrastructure.api.admin.adapter.ApiConverter;
import nextcrowd.crowdfunding.project.ProjectService;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.InvestmentId;

@Controller
public class ProjectController implements AdminApi {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public ResponseEntity<ProjectId> adminProjectsPost(SubmitCrowdfundingProjectCommand submitCrowdfundingProjectCommand) {
        return Optional.of(submitCrowdfundingProjectCommand)
                       .map(ApiConverter::toDomain)
                       .map(projectService::submitProject)
                       .map(nextcrowd.crowdfunding.project.model.ProjectId::id)
                       .map(id -> new ProjectId().id(id))
                       .map(ResponseEntity::ok)
                       .orElseThrow();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdApprovePost(String projectId, ApproveCrowdfundingProjectCommand approveCrowdfundingProjectCommand) {
        projectService.approve(
                new nextcrowd.crowdfunding.project.model.ProjectId(projectId),
                ApiConverter.toDomain(approveCrowdfundingProjectCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdInvestmentsCancelPost(
            String projectId,
            CancelInvestmentCommand cancelInvestmentCommand) {
        projectService.cancelInvestment(new nextcrowd.crowdfunding.project.model.ProjectId(projectId), ApiConverter.toDomain(cancelInvestmentCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdInvestmentsConfirmPost(
            String projectId,
            ConfirmInvestmentCommand confirmInvestmentCommand) {
        projectService.confirmInvestment(
                new nextcrowd.crowdfunding.project.model.ProjectId(projectId),
                ApiConverter.toDomain(confirmInvestmentCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdInvestmentsPost(String projectId, AddInvestmentCommand addInvestmentCommand) {
        projectService.addInvestment(new nextcrowd.crowdfunding.project.model.ProjectId(projectId), ApiConverter.toDomain(addInvestmentCommand));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdIssuePost(String projectId) {
        projectService.issue(new nextcrowd.crowdfunding.project.model.ProjectId(projectId));
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> adminProjectsProjectIdRejectPost(String projectId) {
        projectService.reject(new nextcrowd.crowdfunding.project.model.ProjectId(projectId));
        return ResponseEntity.accepted().build();
    }


    @Override
    public ResponseEntity<PaginatedProjectsResponse> adminProjectsPendingReviewGet(String cursor, Integer limit) {
        List<CrowdfundingProject> results = new ArrayList<>(projectService.getPendingReviewProjects(new nextcrowd.crowdfunding.project.model.ProjectId(cursor))
                                                                          .limit(limit + 1)
                                                                          .map(ApiConverter::toApi)
                                                                          .toList());
        boolean hasMore = results.size() > limit;
        if (hasMore) {
            results.removeLast();
        }
        return ResponseEntity.ok(new PaginatedProjectsResponse()
                                         .data(results)
                                         .hasMore(hasMore));
    }

    @Override
    public ResponseEntity<CrowdfundingProject> adminProjectsProjectIdGet(String projectId) {
        return projectService.getById(new nextcrowd.crowdfunding.project.model.ProjectId(projectId))
                             .map(ApiConverter::toApi)
                             .map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaginatedInvestmentsResponse> adminProjectsProjectIdInvestmentsAcceptedGet(String projectId, String cursor, Integer limit) {
        InvestmentId startingFrom = Optional.ofNullable(cursor)
                                            .map(InvestmentId::new)
                                            .orElse(null);
        List<Investment> results = projectService.getAcceptedInvestments(new nextcrowd.crowdfunding.project.model.ProjectId(
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
        List<Investment> results = new ArrayList<>(projectService.getPendingInvestments(new nextcrowd.crowdfunding.project.model.ProjectId(
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
        List<CrowdfundingProject> results = new ArrayList<>(projectService.getPublishedProjects(new nextcrowd.crowdfunding.project.model.ProjectId(cursor))
                                                          .limit(limit + 1)
                                                          .map(ApiConverter::toApi)
                                                          .toList());
        boolean hasMore = results.size() > limit;
        if (hasMore) {
            results.removeLast();
        }
        return ResponseEntity.ok(new PaginatedProjectsResponse()
                                         .data(results)
                                         .hasMore(hasMore));
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
