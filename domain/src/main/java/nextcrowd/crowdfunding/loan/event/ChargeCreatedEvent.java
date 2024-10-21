package nextcrowd.crowdfunding.loan.event;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.loan.model.ChargeId;
import nextcrowd.crowdfunding.loan.model.DebtorId;
import nextcrowd.crowdfunding.loan.model.LoanId;

@Value
@Builder
public class ChargeCreatedEvent {
    ChargeId id;
    BigDecimal amount;
    LocalDate dueDate;
    DebtorId debtorId;
    LoanId loanId;

}
