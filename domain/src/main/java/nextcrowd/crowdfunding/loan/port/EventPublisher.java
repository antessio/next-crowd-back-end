package nextcrowd.crowdfunding.loan.port;

import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;

public interface EventPublisher {

    void publish(LoanCreatedEvent loanCreatedEvent);

}
