package nextcrowd.crowdfunding.infrastructure.domain.baker.eventpublisher;

import org.springframework.stereotype.Component;

import nextcrowd.crowdfunding.baker.event.BakerCreatedEvent;
import nextcrowd.crowdfunding.baker.port.EventPublisher;
import nextcrowd.crowdfunding.infrastructure.events.DatabaseEventPublisher;

@Component
public class BakerDatabaseEventPublisher implements EventPublisher {
    private final DatabaseEventPublisher databaseEventPublisher;

    public BakerDatabaseEventPublisher(DatabaseEventPublisher databaseEventPublisher) {
        this.databaseEventPublisher = databaseEventPublisher;
    }


    @Override
    public void publish(BakerCreatedEvent bakerCreatedEvent) {
        databaseEventPublisher.publish(bakerCreatedEvent, BakerCreatedEvent.class, bakerCreatedEvent.getBakerId().getId());
    }

}
