package nextcrowd.crowdfunding.infrastructure.domain.project.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the CrowdfundingProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CrowdfundingProjectRepository extends JpaRepository<CrowdfundingProjectEntity, UUID> {


    @Query(value = "SELECT * FROM crowdfunding_project c WHERE c.status IN :statuses ORDER BY c.id ASC LIMIT :size", nativeQuery = true)
    List<CrowdfundingProjectEntity> findByStatusInOrderByIdAsc(@Param("statuses") List<String> statuses, @Param("size") int size);

    @Query(value = "SELECT * FROM crowdfunding_project c WHERE c.status IN :statuses AND c.id > :startingFrom ORDER BY c.id ASC LIMIT :size", nativeQuery = true)
    List<CrowdfundingProjectEntity> findByStatusInOrderByIdAsc(
            @Param("statuses") List<String> statuses,
            @Param("startingFrom") UUID startingFrom,
            @Param("size") int size);

    @Query(value = "SELECT * FROM crowdfunding_project c WHERE c.project_owner_id = :owner_id AND c.status IN :statuses AND c.id > :startingFrom ORDER BY c.id ASC LIMIT :size", nativeQuery = true)
    List<CrowdfundingProjectEntity> findByOwnerIdAndStatusInOrderByIdAsc(
            @Param("owner_id") UUID ownerId,
            @Param("statuses") List<String> statuses,
            @Param("startingFrom") UUID startingFrom,
            @Param("size") int size);

    @Query(value = "SELECT * FROM crowdfunding_project c WHERE c.project_owner_id = :owner_id AND c.id > :startingFrom ORDER BY c.id ASC LIMIT :size", nativeQuery = true)
    List<CrowdfundingProjectEntity> findByOwnerIdInOrderByIdAsc(
            @Param("owner_id") UUID ownerId,
            @Param("startingFrom") UUID startingFrom,
            @Param("size") int size);

    @Query(value = "SELECT * FROM crowdfunding_project c WHERE c.project_owner_id = :owner_id ORDER BY c.id ASC LIMIT :size", nativeQuery = true)
    List<CrowdfundingProjectEntity> findByOwnerIdInOrderByIdAsc(
            @Param("owner_id") UUID ownerId,
            @Param("size") int size);

}
