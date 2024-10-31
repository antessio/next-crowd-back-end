package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Investment {

    private InvestmentId id;
    private BakerId bakerId;
    private BigDecimal amount;
    private MoneyTransferId moneyTransferId;
    private InvestmentStatus status;

    public Investment add(BigDecimal amount) {
        return toBuilder()
                .amount(this.getAmount().add(amount))
                .build();
    }

    public boolean isAccepted() {
        return this.status == InvestmentStatus.ACCEPTED;
    }

    public boolean isRefused() {
        return this.status == InvestmentStatus.REFUSED;
    }

    public boolean isPending() {
        return this.status == InvestmentStatus.PENDING;
    }

    public Investment refuse() {
        return this.toBuilder()
                   .status(InvestmentStatus.REFUSED)
                   .build();
    }

    public Investment accept(MoneyTransferId moneyTransferId) {
        return this.toBuilder()
                   .status(InvestmentStatus.ACCEPTED)
                   .moneyTransferId(moneyTransferId)
                   .build();
    }

}
