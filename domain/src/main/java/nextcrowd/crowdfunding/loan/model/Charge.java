package nextcrowd.crowdfunding.loan.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.loan.port.PaymentServiceChargeId;

@Value
@Builder(toBuilder = true)
public class Charge {
    ChargeId id;
    LocalDate dueDate;
    BigDecimal amount;
    LoanId loanId;
    ChargeStatus status;
    PaymentServiceChargeId paymentServiceChargeId;


    public enum ChargeStatus{
        PENDING,
        PAID
    }

}
