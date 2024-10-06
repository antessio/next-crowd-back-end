package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

@Value
@Builder
public class CrowdfundingProjectApprovedEvent {
    private ProjectId projectId;
    private int risk;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;
}
