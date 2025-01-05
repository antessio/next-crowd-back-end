package nextcrowd.crowdfunding.infrastructure.domain.project.cms.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

@Data
@NoArgsConstructor
public class ProjectOwnerResponse {

    private ProjectOwner data;

    public ProjectOwnerResponse(ProjectOwner data) {
        this.data = data;
    }

}
