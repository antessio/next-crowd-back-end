package nextcrowd.crowdfunding.loan.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Investment {

    LenderId lenderId;
    BigDecimal amount;
    float interestRate;

    public BigDecimal getAmountToReturn() {
        return this.getAmount().add(this.getAmount().multiply(BigDecimal.valueOf(this.getInterestRate()).divide(new BigDecimal(100), RoundingMode.UP)));
    }


}
