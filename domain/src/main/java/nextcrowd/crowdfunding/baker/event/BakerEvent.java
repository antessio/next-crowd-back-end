package nextcrowd.crowdfunding.baker.event;

import lombok.Getter;
import nextcrowd.crowdfunding.baker.model.BakerId;

@Getter
public abstract class BakerEvent {
    protected BakerId bakerId;

    public BakerEvent(BakerId bakerId) {
        this.bakerId = bakerId;
    }

}
