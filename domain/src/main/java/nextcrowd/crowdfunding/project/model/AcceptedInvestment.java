package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AcceptedInvestment {
    private BakerId bakerId;
    private BigDecimal amount;
    private MoneyTransferId moneyTransferId;

}
