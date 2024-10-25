package nextcrowd.crowdfunding.loan.port;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentServiceChargeId createCharge(BigDecimal amount);

    String createChargePaymentLink(PaymentServiceChargeId paymentServiceChargeId);

}
