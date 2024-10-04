package nextcrowd.crowdfunding.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectRewardTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProjectReward getProjectRewardSample1() {
        return new ProjectReward().id(1L).name("name1").imageUrl("imageUrl1").description("description1");
    }

    public static ProjectReward getProjectRewardSample2() {
        return new ProjectReward().id(2L).name("name2").imageUrl("imageUrl2").description("description2");
    }

    public static ProjectReward getProjectRewardRandomSampleGenerator() {
        return new ProjectReward()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
