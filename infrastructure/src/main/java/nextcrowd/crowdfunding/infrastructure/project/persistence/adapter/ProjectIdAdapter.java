package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.UUID;

import nextcrowd.crowdfunding.project.model.ProjectId;


public class ProjectIdAdapter {
    public static ProjectId toDomain(UUID id) {
        return new ProjectId(id.toString());
    }

    public static UUID toEntity(ProjectId id) {
        return UUID.fromString(id.id());
    }

}
