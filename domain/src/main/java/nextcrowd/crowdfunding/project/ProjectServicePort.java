package nextcrowd.crowdfunding.project;

import java.util.Optional;
import java.util.stream.Stream;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;

public interface ProjectServicePort {

    Optional<ProjectContent> getContentById(ProjectId projectId);
    Optional<CrowdfundingProject> getById(ProjectId projectId);

    Stream<CrowdfundingProject> getPublishedProjects(ProjectId startingFrom);

    Stream<CrowdfundingProject> getPendingReviewProjects(ProjectId startingFrom);

    Stream<Investment> getPendingInvestments(ProjectId projectId, InvestmentId startingFrom);

    Stream<Investment> getAcceptedInvestments(ProjectId projectId, InvestmentId startingFrom);

    ProjectId submitProject(SubmitCrowdfundingProjectCommand projectCreationCommand);

    void editProject(ProjectId projectId, EditCrowdfundingProjectCommand editCrowdfundingProjectCommand);

    void approve(ProjectId projectId, ApproveCrowdfundingProjectCommand command);

    void reject(ProjectId projectId);

    void addInvestment(ProjectId projectId, AddInvestmentCommand command);

    void confirmInvestment(ProjectId projectId, ConfirmInvestmentCommand command);

    void cancelInvestment(ProjectId projectId, CancelInvestmentCommand command);

    void issue(ProjectId projectId);

}
