package nextcrowd.crowdfunding.project.port;

import java.util.Optional;

import nextcrowd.crowdfunding.project.model.CreateProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;

public interface CmsPort {

    /**
     * Save the project content to the CMS
     *
     * @param command
     *
     */
    void saveContent(CreateProjectContent command);


    Optional<ProjectContent> getProjectContent(ProjectId projectId);

}
