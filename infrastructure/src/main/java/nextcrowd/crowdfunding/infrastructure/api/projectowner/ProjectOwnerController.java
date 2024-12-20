package nextcrowd.crowdfunding.infrastructure.api.projectowner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nextcrowd.crowdfunding.infrastructure.api.ApiError;
import nextcrowd.crowdfunding.infrastructure.api.projectowner.adapter.ApiConverter;
import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.infrastructure.security.service.SecurityUtils;
import nextcrowd.crowdfunding.project.ProjectServicePort;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectOwnerId;
import nextcrowd.crowdfunding.projectowner.api.ProjectOwnerApi;
import nextcrowd.crowdfunding.projectowner.api.model.CrowdfundingProject;
import nextcrowd.crowdfunding.projectowner.api.model.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.projectowner.api.model.PaginatedProjectsResponse;
import nextcrowd.crowdfunding.projectowner.api.model.ProjectCreated;
import nextcrowd.crowdfunding.projectowner.api.model.SubmitCrowdfundingProjectCommand;

@Controller
public class ProjectOwnerController implements ProjectOwnerApi {

    private final ProjectServicePort projectServicePort;

    public ProjectOwnerController(ProjectServicePort projectServicePort) {
        this.projectServicePort = projectServicePort;
    }

    @Override
    public ResponseEntity<PaginatedProjectsResponse> projectOwnerProjectsGet(String cursor, Integer limit) {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<nextcrowd.crowdfunding.project.model.CrowdfundingProject> result = new ArrayList<>(projectServicePort.getProjectsByProjectOwnerId(
                                                                                                                          new ProjectOwnerId(user.getId().toString()),
                                                                                                                          new ProjectId(cursor))
                                                                                                                  .limit(limit + 1)
                                                                                                                  .toList());
        return convertToPaginatedListWithContent(limit, result);
    }

    @Override
    public ResponseEntity<ProjectCreated> projectOwnerProjectsPost(SubmitCrowdfundingProjectCommand submitCrowdfundingProjectCommand) {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ProjectId projectId = projectServicePort.submitProject(ApiConverter.toDomain(
                submitCrowdfundingProjectCommand, ProjectOwner.builder()
                                                              .id(new ProjectOwnerId(user.getId().toString()))
                                                              .name(user.getFullName())
                                                              // TODO: imageUrl
                                                              .build()));
        return ResponseEntity.ok(new ProjectCreated().id(projectId.id()));
    }

    @Override
    public ResponseEntity<Void> projectOwnerProjectsProjectIdEditPut(String projectId, EditCrowdfundingProjectCommand editCrowdfundingProjectCommand) {
       return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<CrowdfundingProject> projectOwnerProjectsProjectIdGet(String projectId) {
        User user = SecurityUtils.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return projectServicePort.getById(new ProjectId(projectId))
                                 .filter(p -> p.getOwner().getId().equals(new ProjectOwnerId(user.getId().toString())))
                                 .map(p -> nextcrowd.crowdfunding.infrastructure.api.projectowner.adapter.ApiConverter.toApi(p, projectServicePort.getContentById(p.getId()).orElse(null)))
                                 .map(ResponseEntity::ok)
                                 .orElseGet(() -> ResponseEntity.notFound().build());
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


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(CrowdfundingProjectException.class)
    public ResponseEntity<ApiError> handleCrowdfundingProjectException(CrowdfundingProjectException ex) {
        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

}
