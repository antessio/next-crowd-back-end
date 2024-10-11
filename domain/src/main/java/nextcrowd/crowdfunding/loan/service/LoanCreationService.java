package nextcrowd.crowdfunding.loan.service;

import java.util.UUID;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;
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

    public Loan createLoan(LoanCreationCommand command){
        Loan loan = Loan.builder()
                        .id(new LoanId(UUID.randomUUID().toString()))
                        .debtorId(command.getDebtorId())
                        .investments(command.getInvestments())
                        .build();
        loanRepository.save(loan);
        eventPublisher.publish(LoanCreatedEvent.builder()
                                               .id(loan.getId())
                                               .debtorId(loan.getDebtorId())
                                               .build());
        return loan;
    }
}
