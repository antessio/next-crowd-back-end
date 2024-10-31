package nextcrowd.crowdfunding.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentCanceledEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentConfirmedEvent;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.model.AcceptedInvestment;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
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
        checkStatus(project);
        CrowdfundingProject updatedProject = addBaker(project, command);
        repository.save(updatedProject);
        eventPublisher.publish(CrowdfundingProjectPendingInvestmentAddedEvent.builder()
                                                                             .amount(command.getAmount())
                                                                             .bakerId(command.getBakerId())
                                                                             .projectId(project.getId())
                                                                             .build());
    }

    public void confirmInvestment(ConfirmInvestmentCommand command, CrowdfundingProject project) {

        checkStatus(project);
        if (!project.hasConfirmedInvestment(command.getBakerId())) {
            CrowdfundingProject updatedProject = acceptInvestment(project, command.getBakerId(), command.getMoneyTransferId());
            Investment acceptedInvestment = updatedProject.getAcceptedInvestments()
                                                          .stream()
                                                          .filter(i -> i.getBakerId().equals(command.getBakerId()))
                                                          .findFirst()
                                                          .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVESTMENT_NOT_FOUND));
            repository.save(updatedProject);
            eventPublisher.publish(CrowdfundingProjectPendingInvestmentConfirmedEvent
                                           .builder()
                                           .moneyTransferId(acceptedInvestment.getMoneyTransferId())
                                           .projectId(project.getId())
                                           .amount(acceptedInvestment.getAmount())
                                           .bakerId(acceptedInvestment.getBakerId())
                                           .build());
        }
    }

    public void cancelInvestment(CancelInvestmentCommand command, CrowdfundingProject project) {
        checkStatus(project);
        if (!project.hasCanceledInvestment(command.getBakerId())) {
            CrowdfundingProject updatedProject = rejectInvestment(project, command.getBakerId());
            Investment acceptedInvestment = updatedProject.getRefusedInvestments()
                                                          .stream()
                                                          .filter(i -> i.getBakerId().equals(command.getBakerId()))
                                                          .findFirst()
                                                          .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVESTMENT_NOT_FOUND));
            repository.save(updatedProject);
            eventPublisher.publish(CrowdfundingProjectPendingInvestmentCanceledEvent
                                           .builder()
                                           .projectId(project.getId())
                                           .amount(acceptedInvestment.getAmount())
                                           .bakerId(acceptedInvestment.getBakerId())
                                           .build());
        }
    }

    private CrowdfundingProject addBaker(CrowdfundingProject project, AddInvestmentCommand command) {


        List<Investment> newInvestmentsList = findExistingPendingInvestment(project, command.getBakerId())
                .map(existingInvestment -> existingInvestment.add(command.getAmount()))
                .map(updateInvestment -> replaceInvestmentInList(project.getInvestments(), updateInvestment))
                .orElseGet(() -> addNewInvestmentToList(project.getInvestments(), createNewInvestment(command)));

        return project.updateInvestments(newInvestmentsList);
    }

    private Optional<Investment> findExistingPendingInvestment(CrowdfundingProject project, BakerId bakerId) {
        return project.getPendingInvestments()
                      .stream()
                      .filter(investment -> investment.getBakerId().equals(bakerId))
                      .findFirst();
    }

    private List<Investment> replaceInvestmentInList(List<Investment> investments, Investment updatedInvestment) {
        return investments.stream()
                          .map(investment -> investment.getId().equals(updatedInvestment.getId()) ? updatedInvestment : investment)
                          .collect(Collectors.toList());
    }

    private List<Investment> addNewInvestmentToList(List<Investment> investments, Investment newInvestment) {
        List<Investment> updatedList = new ArrayList<>(investments);
        updatedList.add(newInvestment);
        return updatedList;
    }

    private Investment createNewInvestment(AddInvestmentCommand command) {
        return Investment.builder()
                         .id(new InvestmentId(UuidCreator.getTimeOrderedEpoch().toString()))
                         .amount(command.getAmount())
                         .bakerId(command.getBakerId())
                         .status(InvestmentStatus.PENDING)
                         .build();
    }

    private CrowdfundingProject rejectInvestment(CrowdfundingProject project, BakerId bakerId) {
        if (project.hasCanceledInvestment(bakerId)) {
            return project;
        }

        return findExistingPendingInvestment(project, bakerId)
                .map(Investment::refuse)
                .map(refusedInvestment -> replaceInvestmentInList(project.getInvestments(), refusedInvestment))
                .map(project::updateInvestments)
                .orElse(project);

    }

    private CrowdfundingProject acceptInvestment(CrowdfundingProject project, BakerId bakerId, MoneyTransferId moneyTransferId) {
        if (project.hasConfirmedInvestment(bakerId)) {
            return project;
        }
        return findExistingPendingInvestment(project, bakerId)
                .map(existingInvestment -> existingInvestment.accept(moneyTransferId))
                .map(acceptedInvestment -> replaceInvestmentInList(project.getInvestments(), acceptedInvestment))
                .map(project::updateInvestments)
                .orElse(project);
    }

    private void checkStatus(CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.APPROVED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }

}
