package nextcrowd.crowdfunding.infrastructure.events;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectOwnerEntity;


/**
 * Spring Data JPA repository for the CrowdfundingProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {}
