package nextcrowd.crowdfunding.infrastructure.domain.baker.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import nextcrowd.crowdfunding.baker.model.Baker;
import nextcrowd.crowdfunding.baker.model.BakerId;
import nextcrowd.crowdfunding.baker.port.BakerRepository;
import nextcrowd.crowdfunding.infrastructure.domain.baker.persistence.BakerSpringDataRepository;

@Component
public class BakerRepositoryAdapter implements BakerRepository {
    private final BakerSpringDataRepository bakerSpringDataRepository;

    public BakerRepositoryAdapter(BakerSpringDataRepository bakerSpringDataRepository) {
        this.bakerSpringDataRepository = bakerSpringDataRepository;
    }


    @Override
    public Optional<Baker> getBaker(BakerId bakerId) {
        return bakerSpringDataRepository.findById(UUID.fromString(bakerId.getId())).map(BakerAdapter::toDomain);
    }

    @Override
    public void saveBaker(Baker baker) {
        bakerSpringDataRepository.save(BakerAdapter.toEntity(baker));
    }

}
