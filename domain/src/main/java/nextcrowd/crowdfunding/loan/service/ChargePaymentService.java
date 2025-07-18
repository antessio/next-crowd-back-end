package nextcrowd.crowdfunding.loan.service;

import nextcrowd.crowdfunding.loan.model.Charge;
import nextcrowd.crowdfunding.loan.port.ChargeRepository;
import nextcrowd.crowdfunding.loan.port.PaymentService;
import nextcrowd.crowdfunding.loan.port.PaymentServiceChargeId;

public class ChargePaymentService {

    private final PaymentService paymentService;
    private final ChargeRepository chargeRepository;

    public ChargePaymentService(PaymentService paymentService, ChargeRepository chargeRepository) {
        this.paymentService = paymentService;
        this.chargeRepository = chargeRepository;
    }

    public Charge createExternalCharge(Charge charge) {
        if (charge.getPaymentServiceChargeId() != null) {
            return charge;
        }
        PaymentServiceChargeId paymentServiceChargeId = paymentService.createCharge(charge.getAmount());
        return save(charge.toBuilder()
                          .paymentServiceChargeId(paymentServiceChargeId)
                          .build());
    }

    private Charge save(Charge charge) {
        chargeRepository.save(charge);
        return charge;
    }

}
