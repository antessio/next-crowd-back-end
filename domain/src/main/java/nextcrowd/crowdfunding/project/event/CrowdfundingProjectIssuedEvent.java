package nextcrowd.crowdfunding.project.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
public class CrowdfundingProjectIssuedEvent extends CrowdfundingProjectEvent {

    @Builder
    public CrowdfundingProjectIssuedEvent(ProjectId projectId) {
        super(projectId);
    }

}
