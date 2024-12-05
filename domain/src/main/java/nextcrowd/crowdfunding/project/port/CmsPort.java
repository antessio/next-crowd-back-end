package nextcrowd.crowdfunding.project.port;

import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;

public interface CmsPort {

    /**
     * Submits the project to the CMS and attach the generate projectId to it
     *
     * @param command
     *
     * @return
     */
    ProjectId publishProject(SubmitCrowdfundingProjectCommand command);

    CrowdfundingProject getProjectContent(ProjectId projectId);

}
