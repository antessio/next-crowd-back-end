package nextcrowd.crowdfunding.repository;

import nextcrowd.crowdfunding.domain.CrowdfundingProjectOwner;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CrowdfundingProjectOwner entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CrowdfundingProjectOwnerRepository extends JpaRepository<CrowdfundingProjectOwner, Long> {}
