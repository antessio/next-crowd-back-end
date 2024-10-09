package nextcrowd.crowdfunding.loan.model;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Loan {
    private LoanId id;
    private DebtorId debtorId;
    private List<Investment> investments;
}
