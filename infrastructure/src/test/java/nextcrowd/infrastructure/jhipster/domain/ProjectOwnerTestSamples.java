package nextcrowd.infrastructure.jhipster.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectOwnerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProjectOwner getProjectOwnerSample1() {
        return new ProjectOwner().id(1L).name("name1").imageUrl("imageUrl1");
    }

    public static ProjectOwner getProjectOwnerSample2() {
        return new ProjectOwner().id(2L).name("name2").imageUrl("imageUrl2");
    }

    public static ProjectOwner getProjectOwnerRandomSampleGenerator() {
        return new ProjectOwner().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).imageUrl(UUID.randomUUID().toString());
    }
}
