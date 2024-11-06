package nextcrowd.crowdfunding.infrastructure.api.publicwebsite;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import nextcrowd.crowdfunding.infrastructure.api.publicwebsite.adapter.ApiConverter;
import nextcrowd.crowdfunding.project.ProjectService;
import nextcrowd.crowdfunding.websitepublic.api.ProjectsApi;
import nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject;
import nextcrowd.crowdfunding.websitepublic.api.model.PaginatedProjectsResponse;

@Controller
public class ProjectController implements ProjectsApi {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @Override
    public ResponseEntity<CrowdfundingProject> projectsProjectIdGet(String projectId) {
        return projectService.getById(new nextcrowd.crowdfunding.project.model.ProjectId(projectId))
                             .map(ApiConverter::toApi)
                             .map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaginatedProjectsResponse> projectsPublishedGet(String cursor, Integer limit) {
        List<nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject>
                results = new ArrayList<>(projectService.getPublishedProjects(new nextcrowd.crowdfunding.project.model.ProjectId(cursor))
                                                        .limit(limit + 1)
                                                        .map(ApiConverter::toApi)
                                                        .toList());
        boolean hasMore = results.size() > limit;
        if (hasMore) {
            results.removeLast();
        }
        return ResponseEntity.ok(new nextcrowd.crowdfunding.websitepublic.api.model.PaginatedProjectsResponse()
                                         .data(results)
                                         .hasMore(hasMore));
    }

}
