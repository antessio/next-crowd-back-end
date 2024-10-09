package nextcrowd.crowdfunding.loan.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Investment {
    private LenderId lenderId;
    private BigDecimal amount;

}
