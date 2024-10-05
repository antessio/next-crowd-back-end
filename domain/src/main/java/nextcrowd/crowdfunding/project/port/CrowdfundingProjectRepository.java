package nextcrowd.crowdfunding.project.port;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;

public interface CrowdfundingProjectRepository {

    CrowdfundingProject save(CrowdfundingProject project);

}
