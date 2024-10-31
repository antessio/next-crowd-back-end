package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;

@Component
public class CrowdfundingProjectSpringDataRepositoryAdapter implements CrowdfundingProjectRepository {

    private nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectRepository springDataRepository;

    public CrowdfundingProjectSpringDataRepositoryAdapter(nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }


    @Override
    public CrowdfundingProject save(CrowdfundingProject project) {
        return Optional.of(project)
                       .map(CrowdfundingProjectAdapter::toEntity)
                       .map(springDataRepository::save)
                       .map(CrowdfundingProjectAdapter::toDomain)
                       .orElseThrow();
    }

    @Override
    public Optional<CrowdfundingProject> findById(ProjectId id) {
        return springDataRepository.findById(UUID.fromString(id.getId()))
                                   .map(CrowdfundingProjectAdapter::toDomain);
    }

}
