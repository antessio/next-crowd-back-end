package nextcrowd.crowdfunding.loan.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.model.Loan;

public class ChargeService {

    private final Clock clock;

    public ChargeService(Clock clock){
        this.clock = clock;
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

}
