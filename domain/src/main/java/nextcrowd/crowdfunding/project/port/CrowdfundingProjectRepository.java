package nextcrowd.crowdfunding.project.port;

import java.util.Optional;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;

public interface CrowdfundingProjectRepository {

    CrowdfundingProject save(CrowdfundingProject project);

    Optional<CrowdfundingProject> findById(ProjectId id);

}
