package nextcrowd.crowdfunding.project.model;

import com.github.f4b6a3.uuid.UuidCreator;

public record MoneyTransferId(String id) {
    public static MoneyTransferId generate(){
        return new MoneyTransferId(UuidCreator.getTimeOrderedEpoch().toString());
    }

}
