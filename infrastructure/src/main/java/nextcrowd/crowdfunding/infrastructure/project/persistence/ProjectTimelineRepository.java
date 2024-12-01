package nextcrowd.crowdfunding.infrastructure.project.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@SuppressWarnings("unused")
@Repository
public interface ProjectTimelineRepository extends JpaRepository<ProjectTimelineEntity, UUID> {

    Optional<ProjectTimelineEntity> findByProjectId(UUID projectId);

}
