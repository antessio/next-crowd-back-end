package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Investment {
    private BakerId bakerId;
    private BigDecimal amount;

    public Investment add(BigDecimal amount) {
        return toBuilder()
                .amount(this.getAmount().add(amount))
                .build();
    }

}
