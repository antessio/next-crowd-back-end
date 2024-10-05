package nextcrowd.crowdfunding.project.port;

import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;

public interface EventPublisher {

    void publish(CrowdfundingProjectSubmittedEvent crowdfundingProjectSubmittedEventClass);

}
