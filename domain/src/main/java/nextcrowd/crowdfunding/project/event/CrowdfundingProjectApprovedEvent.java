package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

@Value
public class CrowdfundingProjectApprovedEvent extends CrowdfundingProjectEvent {
    private int risk;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;

    @Builder
    public CrowdfundingProjectApprovedEvent(ProjectId projectId, int risk, BigDecimal expectedProfit, BigDecimal minimumInvestment) {
        super(projectId);
        this.risk = risk;
        this.expectedProfit = expectedProfit;
        this.minimumInvestment = minimumInvestment;
    }

}
