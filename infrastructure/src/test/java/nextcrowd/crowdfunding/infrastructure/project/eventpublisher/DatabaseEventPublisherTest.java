package nextcrowd.crowdfunding.infrastructure.project.eventpublisher;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nextcrowd.crowdfunding.infrastructure.events.Event;
import nextcrowd.crowdfunding.infrastructure.events.EventRepository;
import nextcrowd.crowdfunding.infrastructure.events.EventStatus;

@DataJpaTest
@Import({DatabaseEventPublisher.class, ObjectMapper.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Prevent replacing with an embedded DB
class DatabaseEventPublisherTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DatabaseEventPublisher publisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publish() throws JsonProcessingException {
        // Arrange
        TestEvent event = new TestEvent("test");
        String aggregateId = "aggregate-123";

        // Act
        publisher.publish(event, TestEvent.class, aggregateId);

        // Assert
        Optional<Event> savedEventOptional = eventRepository.findAll().stream().findFirst();
        assertTrue(savedEventOptional.isPresent());
        Event savedEvent = savedEventOptional.get();
        assertEquals(TestEvent.class.getName(), savedEvent.getEventType());
        assertEquals(EventStatus.PENDING, savedEvent.getStatus());
        assertEquals(aggregateId, savedEvent.getAggregateId());
        assertFalse(savedEvent.isPublished());
        assertNotNull(savedEvent.getCreatedAt());
        assertEquals(objectMapper.writeValueAsString(event), savedEvent.getPayload());
    }

    static class TestEvent {
        private String name;

        public TestEvent() {
        }

        public TestEvent(String name) {
            this.name = name;
        }

        // getters and setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}