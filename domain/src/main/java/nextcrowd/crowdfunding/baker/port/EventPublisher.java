package nextcrowd.crowdfunding.baker.port;

import nextcrowd.crowdfunding.baker.event.BakerCreatedEvent;

public interface EventPublisher {

    void publish(BakerCreatedEvent bakerCreatedEvent);

}
