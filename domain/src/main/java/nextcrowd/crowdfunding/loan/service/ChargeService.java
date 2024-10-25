package nextcrowd.crowdfunding.loan.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import nextcrowd.crowdfunding.loan.event.ChargePaidEvent;
import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.model.Loan;
import nextcrowd.crowdfunding.loan.port.ChargeRepository;
import nextcrowd.crowdfunding.loan.port.EventPublisher;

public class ChargeService {

    private final Clock clock;
    private final ChargeRepository chargeRepository;
    private final EventPublisher eventPublisher;

    public ChargeService(Clock clock, ChargeRepository chargeRepository, EventPublisher eventPublisher) {
        this.clock = clock;
        this.chargeRepository = chargeRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Charge> createPendingCharges(Loan loan) {

        BigDecimal monthlyAmount = BigDecimal.valueOf(loan.getAmountToReturn().doubleValue() / loan.getDurationInMonths()).setScale(2, RoundingMode.CEILING);
        LocalDate today = LocalDate.now(clock);
        return IntStream.range(0, loan.getDurationInMonths())
                        .mapToObj(index -> today.plusMonths(index + 1))
                        .map(dueDate -> Charge.builder()
                                              .loanId(loan.getId())
                                              .dueDate(dueDate)
                                              .status(Charge.ChargeStatus.PENDING)
                                              .amount(monthlyAmount)
                                              .build())
                        .toList();

    }


    public Charge markAsPaid(Charge charge) {
        if (charge.getPaymentServiceChargeId() == null) {
            throw new IllegalStateException("payment service charge id must be present");
        }
        if (charge.getStatus() == Charge.ChargeStatus.PENDING) {
            Charge paidCharge = charge.toBuilder()
                                      .status(Charge.ChargeStatus.PAID)
                                      .build();
            chargeRepository.save(paidCharge);
            eventPublisher.publish(ChargePaidEvent.builder()
                                                  .id(charge.getId())
                                                  .build());
            return paidCharge;
        }
        return charge;
    }

}
