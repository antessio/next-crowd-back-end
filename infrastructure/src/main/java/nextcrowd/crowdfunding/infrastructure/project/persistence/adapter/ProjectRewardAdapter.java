package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.UUID;

import nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectEntity;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectRewardEntity;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectRewardId;
import nextcrowd.crowdfunding.project.model.ProjectReward;

public class ProjectRewardAdapter {

    public static ProjectReward toDomain(ProjectRewardEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProjectReward.builder()
                            .name(entity.getName())
                            .description(entity.getDescription())
                            .imageUrl(entity.getImageUrl())
                            .build();
    }

    public static ProjectRewardEntity toEntity(ProjectReward domain, CrowdfundingProjectEntity crowdfundingProjectEntity) {
        if (domain == null) {
            return null;
        }
        ProjectRewardEntity entity = new ProjectRewardEntity();
        entity.setId(new ProjectRewardId(domain.getName(), crowdfundingProjectEntity.getId()));
        entity.setCrowdfundingProject(crowdfundingProjectEntity);
        entity.setDescription(domain.getDescription());
        entity.setImageUrl(domain.getImageUrl());
        return entity;
    }

}
