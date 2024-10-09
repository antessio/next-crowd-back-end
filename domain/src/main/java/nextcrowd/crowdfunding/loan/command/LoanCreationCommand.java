package nextcrowd.crowdfunding.loan.command;

import java.util.List;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.loan.model.DebtorId;
import nextcrowd.crowdfunding.loan.model.Investment;

@Builder
@Value
public class LoanCreationCommand {

    private DebtorId debtorId;
    private List<Investment> investments;

}
