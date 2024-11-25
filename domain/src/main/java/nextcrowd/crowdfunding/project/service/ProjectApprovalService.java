package nextcrowd.crowdfunding.project.service;

import java.math.BigDecimal;

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
        CrowdfundingProject approved = approve(project, command.getRisk(), command.getExpectedProfit(), command.getMinimumInvestment());
        repository.save(approved);
        eventPublisher.publish(CrowdfundingProjectApprovedEvent.builder()
                                                               .projectId(approved.getId())
                                                               .minimumInvestment(approved.getMinimumInvestment().orElse(null))
                                                               .risk(approved.getRisk().orElseThrow())
                                                               .expectedProfit(approved.getExpectedProfit().orElse(null))
                                                               .build());
    }
    private static void checkStatus(CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.SUBMITTED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }
    private CrowdfundingProject approve(CrowdfundingProject project, int risk, BigDecimal expectedProfit, BigDecimal minimumInvestment) {
        return project.toBuilder()
                      .risk(risk)
                      .expectedProfit(expectedProfit)
                      .minimumInvestment(minimumInvestment)
                      .status(CrowdfundingProject.Status.APPROVED)
                      .build();

    }

}
