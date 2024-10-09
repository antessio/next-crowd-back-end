package nextcrowd.crowdfunding.project.command;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;

@Value
@Builder
public class AddContributionCommand {
    private BakerId bakerId;
    private BigDecimal amount;

}
