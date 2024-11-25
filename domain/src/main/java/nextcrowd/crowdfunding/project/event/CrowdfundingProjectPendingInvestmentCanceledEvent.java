package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
public class CrowdfundingProjectPendingInvestmentCanceledEvent extends CrowdfundingProjectEvent {
    private BigDecimal amount;
    private BakerId bakerId;

    @Builder
    public CrowdfundingProjectPendingInvestmentCanceledEvent(ProjectId projectId, BigDecimal amount, BakerId bakerId) {
        super(projectId);
        this.amount = amount;
        this.bakerId = bakerId;
    }

}
