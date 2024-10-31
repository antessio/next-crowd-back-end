package nextcrowd.crowdfunding.loan.service;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;
import nextcrowd.crowdfunding.loan.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.model.LoanId;
import nextcrowd.crowdfunding.loan.port.EventPublisher;
import nextcrowd.crowdfunding.loan.port.LoanRepository;

public class LoanCreationService {

    private final LoanRepository loanRepository;
    private final EventPublisher eventPublisher;

    public LoanCreationService(LoanRepository loanRepository, EventPublisher eventPublisher) {
        this.loanRepository = loanRepository;
        this.eventPublisher = eventPublisher;
    }

    public Loan createLoan(LoanCreationCommand command) {
        Loan loan = Loan.builder()
                        .id(new LoanId(UuidCreator.getTimeOrderedEpoch().toString()))
                        .debtorId(command.getDebtorId())
                        .investments(command.getInvestments())
                        .durationInMonths(command.getDurationInMonths())
                        .build();
        loanRepository.save(loan);
        eventPublisher.publish(LoanCreatedEvent.builder()
                                               .id(loan.getId())
                                               .debtorId(loan.getDebtorId())
                                               .build());
        return loan;
    }



}
