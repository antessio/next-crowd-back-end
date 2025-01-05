package nextcrowd.crowdfunding.baker.port;

import java.util.Optional;

import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.model.Baker;

public interface BakerRepository {

    Optional<Baker> getBaker(BakerId bakerId);

    void saveBaker(Baker baker);

}
