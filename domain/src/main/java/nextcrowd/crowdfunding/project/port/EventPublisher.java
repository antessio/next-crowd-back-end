package nextcrowd.crowdfunding.project.port;

import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectIssuedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentConfirmedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;

public interface EventPublisher {

    void publish(CrowdfundingProjectSubmittedEvent crowdfundingProjectSubmittedEvent);

    void publish(CrowdfundingProjectApprovedEvent crowdfundingProjectApprovedEvent);

    void publish(CrowdfundingProjectRejectedEvent crowdfundingProjectRejectedEvent);

    void publish(CrowdfundingProjectPendingInvestmentAddedEvent crowdfundingProjectPendingInvestmentAddedEvent);

    void publish(CrowdfundingProjectIssuedEvent crowdfundingProjectIssuedEvent);

    void publish(CrowdfundingProjectPendingInvestmentConfirmedEvent crowdfundingProjectPendingInvestmentConfirmedEvent);

}
