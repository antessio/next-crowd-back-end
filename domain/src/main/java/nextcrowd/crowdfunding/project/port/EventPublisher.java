package nextcrowd.crowdfunding.project.port;

import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectContributionAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;

public interface EventPublisher {

    void publish(CrowdfundingProjectSubmittedEvent crowdfundingProjectSubmittedEvent);

    void publish(CrowdfundingProjectApprovedEvent crowdfundingProjectApprovedEvent);

    void publish(CrowdfundingProjectRejectedEvent crowdfundingProjectRejectedEvent);

    void publish(CrowdfundingProjectContributionAddedEvent crowdfundingProjectContributionAddedEvent);

}
