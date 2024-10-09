package nextcrowd.crowdfunding.loan.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.loan.model.DebtorId;
import nextcrowd.crowdfunding.loan.model.LoanId;

@Value
@Builder
public class LoanCreatedEvent {
    private LoanId id;
    private DebtorId debtorId;

}
