package nextcrowd.crowdfunding.repository;

import nextcrowd.crowdfunding.domain.ProjectReward;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProjectReward entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRewardRepository extends JpaRepository<ProjectReward, Long> {}
