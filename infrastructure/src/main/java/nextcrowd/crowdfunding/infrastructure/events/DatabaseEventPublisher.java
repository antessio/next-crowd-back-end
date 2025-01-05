package nextcrowd.crowdfunding.infrastructure.events;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;

@Component
public class DatabaseEventPublisher {
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public DatabaseEventPublisher(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    public <T> void publish(T event, Class<T> eventType, String aggregateId) {
        // Save event to database
        try {
            eventRepository.save(Event.builder()
                                      .id(UuidCreator.getTimeOrderedEpoch())
                                      .eventType(eventType.getName())
                                      .status(EventStatus.PENDING)
                                      .aggregateId(aggregateId)
                                      .published(false)
                                      .createdAt(LocalDateTime.now())
                                      .payload(objectMapper.writeValueAsString(event))
                                      .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
