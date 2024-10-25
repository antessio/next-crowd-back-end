package nextcrowd.crowdfunding.loan.port;

import nextcrowd.crowdfunding.loan.event.ChargeCreatedEvent;
import nextcrowd.crowdfunding.loan.event.ChargePaidEvent;
import nextcrowd.crowdfunding.loan.event.LoanCreatedEvent;

public interface EventPublisher {

    void publish(LoanCreatedEvent loanCreatedEvent);

    void publish(ChargeCreatedEvent chargeCreatedEvent);

    void publish(ChargePaidEvent chargePaidEvent);

}
