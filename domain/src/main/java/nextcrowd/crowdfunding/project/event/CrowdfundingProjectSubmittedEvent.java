package nextcrowd.crowdfunding.project.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

@Value
public class CrowdfundingProjectSubmittedEvent extends CrowdfundingProjectEvent {
    private ProjectOwner projectOwner;

    @Builder
    public CrowdfundingProjectSubmittedEvent(ProjectId projectId, ProjectOwner projectOwner) {
        super(projectId);
        this.projectOwner = projectOwner;
    }

}
