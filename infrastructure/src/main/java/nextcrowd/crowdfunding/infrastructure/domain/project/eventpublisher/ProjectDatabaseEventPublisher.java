package nextcrowd.crowdfunding.infrastructure.domain.project.eventpublisher;

import org.springframework.stereotype.Component;

import nextcrowd.crowdfunding.infrastructure.events.DatabaseEventPublisher;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectIssuedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentCanceledEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentConfirmedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.port.EventPublisher;

@Component
public class ProjectDatabaseEventPublisher implements EventPublisher {
    private final DatabaseEventPublisher databaseEventPublisher;

    public ProjectDatabaseEventPublisher(DatabaseEventPublisher databaseEventPublisher) {
        this.databaseEventPublisher = databaseEventPublisher;
    }


    @Override
    public void publish(CrowdfundingProjectSubmittedEvent crowdfundingProjectSubmittedEvent) {
        databaseEventPublisher.publish(crowdfundingProjectSubmittedEvent, CrowdfundingProjectSubmittedEvent.class, crowdfundingProjectSubmittedEvent.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectApprovedEvent crowdfundingProjectApprovedEvent) {
        databaseEventPublisher.publish(crowdfundingProjectApprovedEvent, CrowdfundingProjectApprovedEvent.class, crowdfundingProjectApprovedEvent.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectRejectedEvent event) {
        databaseEventPublisher.publish(event, CrowdfundingProjectRejectedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectPendingInvestmentAddedEvent event) {
        databaseEventPublisher.publish(event, CrowdfundingProjectPendingInvestmentAddedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectIssuedEvent event) {
        databaseEventPublisher.publish(event, CrowdfundingProjectIssuedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectPendingInvestmentConfirmedEvent event) {
        databaseEventPublisher.publish(event, CrowdfundingProjectPendingInvestmentConfirmedEvent.class, event.getProjectId().id());
    }

    @Override
    public void publish(CrowdfundingProjectPendingInvestmentCanceledEvent event) {
        databaseEventPublisher.publish(event, CrowdfundingProjectPendingInvestmentCanceledEvent.class, event.getProjectId().id());
    }

}
