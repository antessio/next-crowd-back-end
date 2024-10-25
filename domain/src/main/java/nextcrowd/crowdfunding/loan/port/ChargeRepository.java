package nextcrowd.crowdfunding.loan.port;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.model.ChargeId;
import nextcrowd.crowdfunding.loan.model.LoanId;

public interface ChargeRepository {

    void save(Charge loan);

    Optional<Charge> findById(ChargeId chargeId);

    Stream<Charge> findByLoanId(LoanId loanId);

    Stream<Charge> findByDueDateBefore(LocalDate targetDate);

    Optional<Charge> findByPaymentServiceChargeId(PaymentServiceChargeId paymentServiceChargeId);

}
