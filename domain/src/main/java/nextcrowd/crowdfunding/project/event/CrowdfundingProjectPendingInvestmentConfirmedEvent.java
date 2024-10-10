package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
@Builder
public class CrowdfundingProjectPendingInvestmentConfirmedEvent {
    private ProjectId projectId;
    private BigDecimal amount;
    private BakerId bakerId;
    private MoneyTransferId moneyTransferId;
}
