package nextcrowd.crowdfunding.project.model;

import com.github.f4b6a3.uuid.UuidCreator;

import lombok.Value;

public record ProjectId(String id) {

    public static ProjectId generateId() {
        return new ProjectId(UuidCreator.getTimeOrderedEpoch().toString());
    }

}
