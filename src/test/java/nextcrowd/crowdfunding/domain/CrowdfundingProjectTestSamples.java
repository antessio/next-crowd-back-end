package nextcrowd.crowdfunding.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CrowdfundingProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CrowdfundingProject getCrowdfundingProjectSample1() {
        return new CrowdfundingProject()
            .id(1L)
            .title("title1")
            .currency("currency1")
            .imageUrl("imageUrl1")
            .risk(1)
            .numberOfBackers(1)
            .summary("summary1")
            .projectVideoUrl("projectVideoUrl1");
    }

    public static CrowdfundingProject getCrowdfundingProjectSample2() {
        return new CrowdfundingProject()
            .id(2L)
            .title("title2")
            .currency("currency2")
            .imageUrl("imageUrl2")
            .risk(2)
            .numberOfBackers(2)
            .summary("summary2")
            .projectVideoUrl("projectVideoUrl2");
    }

    public static CrowdfundingProject getCrowdfundingProjectRandomSampleGenerator() {
        return new CrowdfundingProject()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .risk(intCount.incrementAndGet())
            .numberOfBackers(intCount.incrementAndGet())
            .summary(UUID.randomUUID().toString())
            .projectVideoUrl(UUID.randomUUID().toString());
    }
}
