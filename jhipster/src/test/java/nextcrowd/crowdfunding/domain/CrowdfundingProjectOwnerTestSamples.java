package nextcrowd.crowdfunding.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CrowdfundingProjectOwnerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CrowdfundingProjectOwner getCrowdfundingProjectOwnerSample1() {
        return new CrowdfundingProjectOwner().id(1L).name("name1").imageUrl("imageUrl1");
    }

    public static CrowdfundingProjectOwner getCrowdfundingProjectOwnerSample2() {
        return new CrowdfundingProjectOwner().id(2L).name("name2").imageUrl("imageUrl2");
    }

    public static CrowdfundingProjectOwner getCrowdfundingProjectOwnerRandomSampleGenerator() {
        return new CrowdfundingProjectOwner()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString());
    }
}
