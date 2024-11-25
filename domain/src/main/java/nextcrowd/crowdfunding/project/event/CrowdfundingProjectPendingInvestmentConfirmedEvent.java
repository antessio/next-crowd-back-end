package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
public class CrowdfundingProjectPendingInvestmentConfirmedEvent extends CrowdfundingProjectEvent {
    private BigDecimal amount;
    private BakerId bakerId;
    private MoneyTransferId moneyTransferId;

    @Builder
    public CrowdfundingProjectPendingInvestmentConfirmedEvent(ProjectId projectId, BigDecimal amount, BakerId bakerId, MoneyTransferId moneyTransferId) {
        super(projectId);
        this.amount = amount;
        this.bakerId = bakerId;
        this.moneyTransferId = moneyTransferId;
    }

}
