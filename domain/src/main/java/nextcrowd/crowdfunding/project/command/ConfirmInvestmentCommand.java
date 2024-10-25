package nextcrowd.crowdfunding.project.command;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;

@Value
@Builder
public class ConfirmInvestmentCommand {
    private BakerId bakerId;
    private MoneyTransferId moneyTransferId;

}
