package nextcrowd.crowdfunding.loan.service;

import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.port.PaymentService;
import nextcrowd.crowdfunding.loan.port.PaymentServiceChargeId;

public class ChargePaymentService {

    private final PaymentService paymentService;

    public ChargePaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public Charge createExternalCharge(Charge charge) {
        if (charge.getPaymentServiceChargeId() !=null){
            return charge;
        }
        PaymentServiceChargeId paymentServiceChargeId = paymentService.createCharge(charge.getAmount());
        return charge.toBuilder()
                     .paymentServiceChargeId(paymentServiceChargeId)
                     .build();
    }

}
