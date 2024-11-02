package nextcrowd.crowdfunding.infrastructure.project.persistence;

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
public interface InvestmentRepository extends JpaRepository<InvestmentEntity, UUID> {



    @Query(value = "SELECT * FROM investment i WHERE i.crowdfunding_project_id = :projectId AND i.status IN :investmentStatuses ORDER BY i.id DESC LIMIT :batchSize", nativeQuery = true)
    List<InvestmentEntity> findInvestmentsByStatusesOrderByDesc(UUID projectId, List<String> investmentStatuses, int batchSize);

    @Query(value = "SELECT * FROM investment i WHERE i.crowdfunding_project_id = :projectId AND i.status IN :investmentStatuses and i.id < :startingFrom ORDER BY i.id DESC LIMIT :batchSize", nativeQuery = true)
    List<InvestmentEntity> findInvestmentsByStatusesOrderByDesc(UUID projectId, List<String> investmentStatuses, UUID startingFrom, int batchSize);

}
