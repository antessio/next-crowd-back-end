package nextcrowd.crowdfunding.project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        CrowdfundingProject updatedProject = addBaker(project, Investment.builder()
                                                                         .bakerId(command.getBakerId())
                                                                         .amount(command.getAmount()).build());
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
            AcceptedInvestment acceptedInvestment = updatedProject.getAcceptedInvestments()
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

    private CrowdfundingProject addBaker(CrowdfundingProject project, Investment investment) {

        Map<BakerId, Investment> investmentsByBaker = Optional.ofNullable(project.getPendingInvestments())
                                                              .map(investmentList -> investmentList
                                                                      .stream()
                                                                      .collect(Collectors.toMap(Investment::getBakerId, Function.identity())))
                                                              .orElseGet(HashMap::new);
        Investment investmentToAdd = Optional.ofNullable(investmentsByBaker.get(investment.getBakerId()))
                                             .map(i -> i.add(investment.getAmount()))
                                             .orElse(investment);

        investmentsByBaker.put(investmentToAdd.getBakerId(), investmentToAdd);
        return project.toBuilder()
                      .pendingInvestments(new ArrayList<>(investmentsByBaker.values()))
                      .build();
    }

    private CrowdfundingProject rejectInvestment(CrowdfundingProject project, BakerId bakerId) {
        if (project.hasCanceledInvestment(bakerId)) {
            return project;
        }
        return project.getPendingInvestments()
                      .stream()
                      .filter(i -> i.getBakerId().equals(bakerId))
                      .findFirst()
                      .map(investment -> {
                          List<Investment> refusedInvestmentsToUpDate = new ArrayList<>(project.getRefusedInvestments());
                          refusedInvestmentsToUpDate.add(investment);
                          return project.toBuilder()
                                        .pendingInvestments(project.getPendingInvestments().stream().filter(i -> !i.equals(investment)).toList())
                                        .refusedInvestments(refusedInvestmentsToUpDate)
                                        .build();

                      }).orElse(project);
    }
    private CrowdfundingProject acceptInvestment(CrowdfundingProject project, BakerId bakerId, MoneyTransferId moneyTransferId) {
        if (project.hasConfirmedInvestment(bakerId)) {
            return project;
        }
        return project.getPendingInvestments()
                      .stream()
                      .filter(i -> i.getBakerId().equals(bakerId))
                      .findFirst()
                      .map(pendingInvestment -> {
                          List<AcceptedInvestment> acceptedInvestmentsToUpDate = new ArrayList<>(project.getAcceptedInvestments());
                          AcceptedInvestment acceptedInvestment = AcceptedInvestment.builder()
                                                                                    .bakerId(pendingInvestment.getBakerId())
                                                                                    .amount(pendingInvestment.getAmount())
                                                                                    .moneyTransferId(moneyTransferId)
                                                                                    .build();
                          acceptedInvestmentsToUpDate.add(acceptedInvestment);
                          return project.toBuilder()
                                        .pendingInvestments(project.getPendingInvestments().stream().filter(i -> !i.equals(pendingInvestment)).toList())
                                        .acceptedInvestments(acceptedInvestmentsToUpDate)
                                        .collectedAmount(project.getCollectedAmount().add(acceptedInvestment.getAmount()))
                                        .build();

                      }).orElse(project);

    }
    private void checkStatus(CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.APPROVED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }

}
