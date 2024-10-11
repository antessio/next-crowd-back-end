package nextcrowd.crowdfunding.project.service;

import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;

public class ProjectApprovalService {

    private final EventPublisher eventPublisher;
    private final CrowdfundingProjectRepository repository;

    public ProjectApprovalService(EventPublisher eventPublisher, CrowdfundingProjectRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    public void approve(ApproveCrowdfundingProjectCommand command, CrowdfundingProject project) {
        if (project.getStatus() == CrowdfundingProject.Status.APPROVED) {
            return;
        }
        checkStatus(project);
        CrowdfundingProject approved = project.approve(command.getRisk(), command.getExpectedProfit(), command.getMinimumInvestment());
        repository.save(approved);
        eventPublisher.publish(CrowdfundingProjectApprovedEvent.builder()
                                                               .projectId(approved.getId())
                                                               .minimumInvestment(approved.getMinimumInvestment())
                                                               .risk(approved.getRisk())
                                                               .expectedProfit(approved.getExpectedProfit())
                                                               .build());
    }
    private static void checkStatus(CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.SUBMITTED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }

}
