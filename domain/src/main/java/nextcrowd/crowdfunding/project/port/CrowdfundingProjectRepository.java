package nextcrowd.crowdfunding.project.port;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectOwnerId;

public interface CrowdfundingProjectRepository {

    CrowdfundingProject save(CrowdfundingProject project);

    ProjectOwner createProjectOwner(ProjectOwner projectOwner);

    Optional<CrowdfundingProject> findById(ProjectId id);

    Stream<CrowdfundingProject> findByStatusesOrderByAsc(Set<CrowdfundingProject.Status> statuses, ProjectId startingFrom);

    Stream<Investment> findInvestmentsByStatusesOrderByDesc(ProjectId projectId, InvestmentId startingFrom, Set<InvestmentStatus> investmentStatuses);

    Optional<ProjectOwner> findOwnerById(ProjectOwnerId id);

    Stream<CrowdfundingProject> findByStatusesOrderByAsc(ProjectOwnerId projectOwnerId, Set<CrowdfundingProject.Status> statuses, ProjectId startingFrom);

}
