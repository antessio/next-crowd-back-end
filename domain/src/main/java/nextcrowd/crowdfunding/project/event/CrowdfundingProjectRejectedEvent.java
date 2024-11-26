package nextcrowd.crowdfunding.project.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
public class CrowdfundingProjectRejectedEvent extends CrowdfundingProjectEvent {

    @Builder
    public CrowdfundingProjectRejectedEvent(ProjectId projectId) {
        super(projectId);
    }

}
