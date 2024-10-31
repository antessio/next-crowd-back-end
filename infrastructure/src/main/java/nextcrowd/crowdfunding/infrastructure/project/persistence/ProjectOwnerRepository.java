package nextcrowd.crowdfunding.infrastructure.project.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the CrowdfundingProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectOwnerRepository extends JpaRepository<ProjectOwnerEntity, UUID> {}
