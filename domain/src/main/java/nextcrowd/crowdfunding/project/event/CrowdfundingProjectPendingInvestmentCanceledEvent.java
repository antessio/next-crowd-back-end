package nextcrowd.crowdfunding.project.event;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
@Builder
public class CrowdfundingProjectPendingInvestmentCanceledEvent {
    private ProjectId projectId;
    private BigDecimal amount;
    private BakerId bakerId;
}
