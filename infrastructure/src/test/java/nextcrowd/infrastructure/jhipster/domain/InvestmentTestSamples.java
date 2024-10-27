package nextcrowd.infrastructure.jhipster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InvestmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Investment getInvestmentSample1() {
        return new Investment().id(1L).bakerId("bakerId1").moneyTransferId("moneyTransferId1");
    }

    public static Investment getInvestmentSample2() {
        return new Investment().id(2L).bakerId("bakerId2").moneyTransferId("moneyTransferId2");
    }

    public static Investment getInvestmentRandomSampleGenerator() {
        return new Investment()
            .id(longCount.incrementAndGet())
            .bakerId(UUID.randomUUID().toString())
            .moneyTransferId(UUID.randomUUID().toString());
    }
}
