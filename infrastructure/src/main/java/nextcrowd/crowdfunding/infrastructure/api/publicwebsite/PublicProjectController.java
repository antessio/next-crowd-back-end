package nextcrowd.crowdfunding.infrastructure.api.publicwebsite;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import nextcrowd.crowdfunding.infrastructure.api.publicwebsite.adapter.ApiConverter;
import nextcrowd.crowdfunding.infrastructure.storage.FileStorageService;
import nextcrowd.crowdfunding.project.ProjectServicePort;
import nextcrowd.crowdfunding.websitepublic.api.PublicApi;
import nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject;
import nextcrowd.crowdfunding.websitepublic.api.model.PaginatedProjectsResponse;

@Controller
public class PublicProjectController implements PublicApi {

    private final ProjectServicePort projectServicePort;
    private final FileStorageService fileStorageService;

    public PublicProjectController(ProjectServicePort projectServicePort, FileStorageService fileStorageService) {
        this.projectServicePort = projectServicePort;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public ResponseEntity<CrowdfundingProject> publicProjectsProjectIdGet(String projectId) {
        return projectServicePort.getById(new nextcrowd.crowdfunding.project.model.ProjectId(projectId))
                                 .map(ApiConverter::toApi)
                                 .map(ResponseEntity::ok)
                                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PaginatedProjectsResponse> publicProjectsPublishedGet(String cursor, Integer limit) {
        List<nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject>
                results = new ArrayList<>(projectServicePort.getPublishedProjects(new nextcrowd.crowdfunding.project.model.ProjectId(cursor))
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

    @Override
    public ResponseEntity<Resource> publicImageIdGet(String id) {
        return fileStorageService.load(id)
                                 .map(storageResource -> ResponseEntity.ok()
                                                                       .contentLength(storageResource.content().length)
                                                                       .contentType(MediaType.parseMediaType(storageResource.contentType()))
                                                                       .body(fromStorage(storageResource)))
                                 .orElseGet(() -> ResponseEntity.notFound().build());
    }


    private static Resource fromStorage(FileStorageService.StorageResource storageResource) {
        return new ByteArrayResource(storageResource.content());
    }

    @Override
    public ResponseEntity<Resource> publicVideoIdGet(String id) {
        return fileStorageService.load(id)
                                 .map(storageResource -> ResponseEntity.ok()
                                                                       .contentLength(storageResource.content().length)
                                                                       .contentType(MediaType.parseMediaType(storageResource.contentType()))
                                                                       .body(fromStorage(storageResource)))
                                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
