package nextcrowd.crowdfunding.infrastructure.domain.project.persistence.adapter;

import nextcrowd.crowdfunding.infrastructure.domain.project.persistence.ProjectOwnerEntity;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectOwnerId;

import java.util.UUID;

public class ProjectOwnerAdapter {

    public static ProjectOwner toDomain(ProjectOwnerEntity entity) {
        return ProjectOwner.builder()
                           .id(new ProjectOwnerId(entity.getId().toString()))
                           .name(entity.getName())
                           .build();
    }

    public static ProjectOwnerEntity toEntity(ProjectOwner owner) {
        return ProjectOwnerEntity.builder()
                                 .id(UUID.fromString(owner.getId().id()))
                                 .name(owner.getName())
                                 .build();
    }
}