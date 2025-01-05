package nextcrowd.crowdfunding.infrastructure.events;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nextcrowd.crowdfunding.infrastructure.BaseTestWithTestcontainers;
import nextcrowd.crowdfunding.infrastructure.domain.project.eventpublisher.ProjectDatabaseEventPublisher;

@DataJpaTest
@Import({ProjectDatabaseEventPublisher.class, ObjectMapper.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Prevent replacing with an embedded DB
class DatabaseEventPublisherTest extends BaseTestWithTestcontainers {

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
        List<Event> allEvents = eventRepository.findAll();
        Optional<Event> savedEventOptional = allEvents.stream().findFirst();
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