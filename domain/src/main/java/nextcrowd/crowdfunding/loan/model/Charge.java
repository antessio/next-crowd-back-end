package nextcrowd.crowdfunding.loan.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Charge {
    ChargeId id;
    LocalDate dueDate;
    BigDecimal amount;
    LoanId loanId;
    ChargeStatus status;

    public enum ChargeStatus{
        PENDING,
        PAID
    }

}
