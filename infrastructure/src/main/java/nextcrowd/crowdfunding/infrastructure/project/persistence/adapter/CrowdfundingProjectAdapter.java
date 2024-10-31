package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectEntity;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;

public class CrowdfundingProjectAdapter {

    // Convert from Entity to Domain Object
    public static CrowdfundingProject toDomain(CrowdfundingProjectEntity entity) {
        return CrowdfundingProject.builder()
                                  .id(new ProjectId(entity.getId().toString()))
                                  .title(entity.getTitle())
                                  .status(CrowdfundingProject.Status.valueOf(entity.getStatus().name()))
                                  .requestedAmount(entity.getRequestedAmount())
                                  .collectedAmount(entity.getCollectedAmount())
                                  .currency(entity.getCurrency())
                                  .imageUrl(entity.getImageUrl())
                                  .owner(ProjectOwnerAdapter.toDomain(entity.getProjectOwner()))
                                  .projectStartDate(entity.getProjectStartDate())
                                  .projectEndDate(entity.getProjectEndDate())
                                  .numberOfBackers(entity.getNumberOfBackers() != null ? entity.getNumberOfBackers() : 0)
                                  .description(entity.getDescription())
                                  .longDescription(entity.getLongDescription())
                                  .projectVideoUrl(entity.getProjectVideoUrl())
                                  .risk(entity.getRisk() != null ? entity.getRisk() : 0)
                                  .expectedProfit(entity.getExpectedProfit())
                                  .minimumInvestment(entity.getMinimumInvestment())
                                  .rewards(entity.getRewards().stream().map(ProjectRewardAdapter::toDomain).toList())
                                  .investments(entity.getInvestments().stream().map(InvestmentAdapter::toDomain).toList())
                                  .build();
    }

    // Convert from Domain Object to Entity
    public static CrowdfundingProjectEntity toEntity(CrowdfundingProject project) {
        UUID projectId = UUID.fromString(project.getId().getId());
        CrowdfundingProjectEntity entity = CrowdfundingProjectEntity.builder()
                                                                    .id(projectId)
                                                                    .title(project.getTitle())
                                                                    .status(project.getStatus())
                                                                    .requestedAmount(project.getRequestedAmount())
                                                                    .collectedAmount(project.getCollectedAmount())
                                                                    .currency(project.getCurrency())
                                                                    .imageUrl(project.getImageUrl())
                                                                    .projectOwner(ProjectOwnerAdapter.toEntity(project.getOwner()))
                                                                    .projectStartDate(project.getProjectStartDate())
                                                                    .projectEndDate(project.getProjectEndDate())
                                                                    .numberOfBackers(project.getNumberOfBackers())
                                                                    .description(project.getDescription())
                                                                    .longDescription(project.getLongDescription())
                                                                    .projectVideoUrl(project.getProjectVideoUrl())
                                                                    .risk(project.getRisk())
                                                                    .expectedProfit(project.getExpectedProfit())
                                                                    .minimumInvestment(project.getMinimumInvestment())
                                                                    .rewards(new HashSet<>())
                                                                    .investments(Optional.ofNullable(project.getInvestments())
                                                                                         .stream()
                                                                                         .flatMap(List::stream)
                                                                                         .map(InvestmentAdapter::toEntity)
                                                                                         .collect(Collectors.toSet()))
                                                                    .build();

        entity.getRewards().addAll(project.getRewards()
                                          .stream()
                                          .map(r -> ProjectRewardAdapter.toEntity(r, entity))
                                          .collect(Collectors.toSet()));
        return entity;
    }


}
