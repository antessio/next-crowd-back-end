package nextcrowd.crowdfunding.infrastructure.domain.project.persistence.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.CrowdfundingProjectEntity;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;

public class CrowdfundingProjectAdapter {

    // Convert from Entity to Domain Object
    public static CrowdfundingProject toDomain(CrowdfundingProjectEntity entity) {
        return CrowdfundingProject.builder()
                                  .id(new ProjectId(entity.getId().toString()))
                                  .status(CrowdfundingProject.Status.valueOf(entity.getStatus().name()))
                                  .requestedAmount(entity.getRequestedAmount())
                                  .collectedAmount(entity.getCollectedAmount())
                                  .currency(entity.getCurrency())
                                  .owner(ProjectOwnerAdapter.toDomain(entity.getProjectOwner()))
                                  .projectStartDate(entity.getProjectStartDate())
                                  .projectEndDate(entity.getProjectEndDate())
                                  .numberOfBackers(entity.getNumberOfBackers())
                                  .risk(entity.getRisk())
                                  .expectedProfit(entity.getExpectedProfit())
                                  .minimumInvestment(entity.getMinimumInvestment())
                                  .investments(entity.getInvestments().stream().map(InvestmentAdapter::toDomain).toList())
                                  .build();
    }

    // Convert from Domain Object to Entity
    public static CrowdfundingProjectEntity toEntity(CrowdfundingProject project) {
        UUID projectId = UUID.fromString(project.getId().id());
        CrowdfundingProjectEntity entity = CrowdfundingProjectEntity.builder()
                                                                    .id(projectId)
                                                                    .status(project.getStatus())
                                                                    .requestedAmount(project.getRequestedAmount())
                                                                    .collectedAmount(project.getCollectedAmount().orElse(null))
                                                                    .currency(project.getCurrency())
                                                                    .projectOwner(ProjectOwnerAdapter.toEntity(project.getOwner()))
                                                                    .projectStartDate(project.getProjectStartDate())
                                                                    .projectEndDate(project.getProjectEndDate())
                                                                    .numberOfBackers(project.getNumberOfBackers().orElse(null))
                                                                    .risk(project.getRisk().orElse(null))
                                                                    .expectedProfit(project.getExpectedProfit().orElse(null))
                                                                    .minimumInvestment(project.getMinimumInvestment().orElse(null))
                                                                    .rewards(new HashSet<>())
                                                                    .investments(Optional.ofNullable(project.getInvestments())
                                                                                         .stream()
                                                                                         .flatMap(List::stream)
                                                                                         .map(InvestmentAdapter::toEntity)
                                                                                         .collect(Collectors.toSet()))
                                                                    .build();
        return entity;
    }


}
