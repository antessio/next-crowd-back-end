package nextcrowd.crowdfunding.project.model;

import com.github.f4b6a3.uuid.UuidCreator;

public record BakerId(String id) {

    public static BakerId generate() {
        return new BakerId(UuidCreator.getTimeOrderedEpoch().toString());
    }

}
