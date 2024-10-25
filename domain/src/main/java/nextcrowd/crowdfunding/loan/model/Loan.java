package nextcrowd.crowdfunding.loan.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Loan {

    LoanId id;
    DebtorId debtorId;
    List<Investment> investments;
    boolean badDebt;
    int durationInMonths;

    public BigDecimal getAmount() {
        return Optional.ofNullable(investments)
                       .stream()
                       .flatMap(List::stream)
                       .map(Investment::getAmount)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAmountToReturn() {
        return Optional.ofNullable(investments)
                       .stream()
                       .flatMap(List::stream)
                       .map(Investment::getAmountToReturn)
                       .reduce(BigDecimal.ZERO, BigDecimal::add);
    }



}
