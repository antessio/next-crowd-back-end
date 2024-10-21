package nextcrowd.crowdfunding.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.List;

import nextcrowd.crowdfunding.loan.command.LoanCreationCommand;
import nextcrowd.crowdfunding.loan.event.ChargeCreatedEvent;
import nextcrowd.crowdfunding.loan.exception.LoanException;
import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.model.LoanId;
import nextcrowd.crowdfunding.loan.port.ChargeRepository;
import nextcrowd.crowdfunding.loan.port.EventPublisher;
import nextcrowd.crowdfunding.loan.port.LoanRepository;
import nextcrowd.crowdfunding.loan.service.ChargeService;
import nextcrowd.crowdfunding.loan.service.LoanCreationService;

public class LoanService {


    private final LoanRepository loanRepository;
    private final ChargeRepository chargeRepository;
    private final LoanCreationService loanCreationService;
    private final ChargeService chargeService;
    private final EventPublisher eventPublisher;

    public LoanService(LoanRepository loanRepository, ChargeRepository chargeRepository, Clock clock, EventPublisher eventPublisher) {
        this.loanRepository = loanRepository;
        this.chargeRepository = chargeRepository;
        this.loanCreationService = new LoanCreationService(loanRepository, eventPublisher);
        this.chargeService = new ChargeService(clock);
        this.eventPublisher = eventPublisher;
    }

    public Loan createLoan(LoanCreationCommand command) {
        return loanCreationService.createLoan(command);
    }

    public void createCharges(LoanId loanId) {
        Loan loan = loanRepository.findById(loanId)
                                  .orElseThrow(() -> new LoanException(LoanException.Reason.LOAN_NOT_FOUND));

        List<Charge> charges = chargeService.createPendingCharges(loan);
        charges.forEach(c -> {
            chargeRepository.save(c);
            eventPublisher.publish(ChargeCreatedEvent.builder()
                                                     .id(c.getId())
                                                     .dueDate(c.getDueDate())
                                                     .amount(c.getAmount())
                                                     .loanId(loan.getId())
                                                     .debtorId(loan.getDebtorId())
                                                     .build());
        });
    }

}
