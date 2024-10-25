package nextcrowd.crowdfunding.loan;

import java.time.Clock;
import java.time.LocalDate;
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
import nextcrowd.crowdfunding.loan.port.PaymentService;
import nextcrowd.crowdfunding.loan.service.ChargePaymentService;
import nextcrowd.crowdfunding.loan.service.ChargeService;
import nextcrowd.crowdfunding.loan.service.LoanCreationService;

public class LoanService {


    private final LoanRepository loanRepository;
    private final ChargeRepository chargeRepository;
    private final LoanCreationService loanCreationService;
    private final ChargeService chargeService;
    private final EventPublisher eventPublisher;
    private final ChargePaymentService chargePaymentService;

    public LoanService(
            LoanRepository loanRepository, ChargeRepository chargeRepository,
            PaymentService paymentService,
            Clock clock, EventPublisher eventPublisher) {
        this.loanRepository = loanRepository;
        this.chargeRepository = chargeRepository;
        this.loanCreationService = new LoanCreationService(loanRepository, eventPublisher);
        this.chargeService = new ChargeService(clock);
        this.eventPublisher = eventPublisher;
        this.chargePaymentService = new ChargePaymentService(paymentService);
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

    public void performCharges(LocalDate targetDate) {
        chargeRepository.findByDueDateBefore(targetDate)
                        .filter(c -> c.getPaymentServiceChargeId() == null)
                        .map(chargePaymentService::createExternalCharge)
                        .forEach(chargeRepository::save);
    }

}
