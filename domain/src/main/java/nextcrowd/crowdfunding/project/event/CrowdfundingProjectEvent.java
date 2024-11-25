package nextcrowd.crowdfunding.project.event;

import lombok.Getter;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Getter
public abstract class CrowdfundingProjectEvent {
    protected ProjectId projectId;

    public CrowdfundingProjectEvent(ProjectId projectId) {
        this.projectId = projectId;
    }

}
