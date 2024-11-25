package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
public class CrowdfundingProjectPendingInvestmentAddedEvent extends CrowdfundingProjectEvent {
    private BigDecimal amount;
    private BakerId bakerId;

    @Builder
    public CrowdfundingProjectPendingInvestmentAddedEvent(ProjectId projectId, BigDecimal amount, BakerId bakerId) {
        super(projectId);
        this.amount = amount;
        this.bakerId = bakerId;
    }

}
