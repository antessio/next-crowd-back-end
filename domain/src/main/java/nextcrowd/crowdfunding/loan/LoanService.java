package nextcrowd.crowdfunding.loan;

import java.util.UUID;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;
import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.model.LoanId;
import nextcrowd.crowdfunding.loan.port.EventPublisher;
import nextcrowd.crowdfunding.loan.port.LoanRepository;
import nextcrowd.crowdfunding.loan.service.LoanCreationService;

public class LoanService {


    private final LoanRepository loanRepository;
    private final LoanCreationService loanCreationService;

    public LoanService(LoanRepository loanRepository, EventPublisher eventPublisher) {
        this.loanRepository = loanRepository;
        this.loanCreationService = new LoanCreationService(loanRepository, eventPublisher);
    }

    public Loan createLoan(LoanCreationCommand command) {
        return loanCreationService.createLoan(command);
    }

}
