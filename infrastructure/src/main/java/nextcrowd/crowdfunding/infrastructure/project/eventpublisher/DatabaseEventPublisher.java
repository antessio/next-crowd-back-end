package nextcrowd.crowdfunding.infrastructure.project.eventpublisher;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.infrastructure.events.Event;
import nextcrowd.crowdfunding.infrastructure.events.EventRepository;
import nextcrowd.crowdfunding.infrastructure.events.EventStatus;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectIssuedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentCanceledEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentConfirmedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.port.EventPublisher;

@Component
public class DatabaseEventPublisher implements EventPublisher {

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

    @Override
    public void publish(CrowdfundingProjectSubmittedEvent crowdfundingProjectSubmittedEvent) {
        publish(crowdfundingProjectSubmittedEvent, CrowdfundingProjectSubmittedEvent.class, crowdfundingProjectSubmittedEvent.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectApprovedEvent crowdfundingProjectApprovedEvent) {
        publish(crowdfundingProjectApprovedEvent, CrowdfundingProjectApprovedEvent.class, crowdfundingProjectApprovedEvent.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectRejectedEvent event) {
        publish(event, CrowdfundingProjectRejectedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectPendingInvestmentAddedEvent event) {
        publish(event, CrowdfundingProjectPendingInvestmentAddedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectIssuedEvent event) {
        publish(event, CrowdfundingProjectIssuedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectPendingInvestmentConfirmedEvent event) {
        publish(event, CrowdfundingProjectPendingInvestmentConfirmedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectPendingInvestmentCanceledEvent event) {
        publish(event, CrowdfundingProjectPendingInvestmentCanceledEvent.class, event.getProjectId().id());
    }

}
