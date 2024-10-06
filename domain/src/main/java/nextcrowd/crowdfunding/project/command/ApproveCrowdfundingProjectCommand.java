package nextcrowd.crowdfunding.project.command;


import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApproveCrowdfundingProjectCommand {
    private int risk;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;
}

