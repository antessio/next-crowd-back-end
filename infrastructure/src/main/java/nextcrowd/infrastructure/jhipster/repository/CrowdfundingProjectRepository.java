package nextcrowd.infrastructure.jhipster.repository;

import nextcrowd.infrastructure.jhipster.domain.CrowdfundingProject;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CrowdfundingProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CrowdfundingProjectRepository extends JpaRepository<CrowdfundingProject, Long> {}
