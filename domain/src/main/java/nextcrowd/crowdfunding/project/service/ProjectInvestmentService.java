package nextcrowd.crowdfunding.project.service;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentCanceledEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentConfirmedEvent;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.model.AcceptedInvestment;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;

public class ProjectInvestmentService {

    private final EventPublisher eventPublisher;
    private final CrowdfundingProjectRepository repository;

    public ProjectInvestmentService(EventPublisher eventPublisher, CrowdfundingProjectRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    public void addInvestment(AddInvestmentCommand command, CrowdfundingProject project) {
        checkStatus(project, CrowdfundingProject.Status.APPROVED);
        CrowdfundingProject updatedProject = project.addBaker(Investment.builder()
                                                                        .bakerId(command.getBakerId())
                                                                        .amount(command.getAmount()).build());
        repository.save(updatedProject);
        eventPublisher.publish(CrowdfundingProjectPendingInvestmentAddedEvent.builder()
                                                                             .amount(command.getAmount())
                                                                             .bakerId(command.getBakerId())
                                                                             .projectId(project.getId())
                                                                             .build());
    }

    public void confirmInvestment(ProjectId projectId, ConfirmInvestmentCommand command, CrowdfundingProject project) {

        checkStatus(project, CrowdfundingProject.Status.APPROVED);
        if (!project.hasConfirmedInvestment(command.getBakerId())) {
            CrowdfundingProject updatedProject = project.acceptInvestment(command.getBakerId(), command.getMoneyTransferId());
            AcceptedInvestment acceptedInvestment = updatedProject.getAcceptedInvestments()
                                                                  .stream()
                                                                  .filter(i -> i.getBakerId().equals(command.getBakerId()))
                                                                  .findFirst()
                                                                  .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVESTMENT_NOT_FOUND));
            repository.save(updatedProject);
            eventPublisher.publish(CrowdfundingProjectPendingInvestmentConfirmedEvent
                                           .builder()
                                           .moneyTransferId(acceptedInvestment.getMoneyTransferId())
                                           .projectId(projectId)
                                           .amount(acceptedInvestment.getAmount())
                                           .bakerId(acceptedInvestment.getBakerId())
                                           .build());
        }
    }

    private static void checkStatus(CrowdfundingProject project, CrowdfundingProject.Status targetStatus) {
        if (project.getStatus() != targetStatus) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }

    public void cancelInvestment(ProjectId projectId, CancelInvestmentCommand command, CrowdfundingProject project) {
        checkStatus(project, CrowdfundingProject.Status.APPROVED);
        if (!project.hasCanceledInvestment(command.getBakerId())) {
            CrowdfundingProject updatedProject = project.rejectInvestment(command.getBakerId());
            Investment acceptedInvestment = updatedProject.getRefusedInvestments()
                                                                  .stream()
                                                                  .filter(i -> i.getBakerId().equals(command.getBakerId()))
                                                                  .findFirst()
                                                                  .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVESTMENT_NOT_FOUND));
            repository.save(updatedProject);
            eventPublisher.publish(CrowdfundingProjectPendingInvestmentCanceledEvent
                                           .builder()
                                           .projectId(projectId)
                                           .amount(acceptedInvestment.getAmount())
                                           .bakerId(acceptedInvestment.getBakerId())
                                           .build());
        }
    }

}
