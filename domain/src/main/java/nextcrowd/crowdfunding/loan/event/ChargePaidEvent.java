package nextcrowd.crowdfunding.loan.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.loan.model.ChargeId;

@Value
@Builder
public class ChargePaidEvent {
    ChargeId id;

}
