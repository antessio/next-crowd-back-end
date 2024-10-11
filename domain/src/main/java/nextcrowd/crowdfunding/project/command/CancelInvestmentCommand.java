package nextcrowd.crowdfunding.project.command;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;

@Value
@Builder
public class CancelInvestmentCommand {
    private BakerId bakerId;

}
