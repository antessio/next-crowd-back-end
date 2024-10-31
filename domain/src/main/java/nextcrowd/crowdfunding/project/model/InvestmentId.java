package nextcrowd.crowdfunding.project.model;

import com.github.f4b6a3.uuid.UuidCreator;

public record InvestmentId(String id) {

    public static InvestmentId generate() {
        return new InvestmentId(UuidCreator.getTimeOrderedEpoch().toString());
    }

}
