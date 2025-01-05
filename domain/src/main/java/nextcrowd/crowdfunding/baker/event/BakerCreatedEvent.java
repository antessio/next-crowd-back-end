package nextcrowd.crowdfunding.baker.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.RiskLevel;

@Value
public class BakerCreatedEvent extends BakerEvent {

    RiskLevel riskLevel;

    @Builder
    public BakerCreatedEvent(BakerId bakerId, RiskLevel riskLevel) {
        super(bakerId);
        this.riskLevel = riskLevel;
    }

}
