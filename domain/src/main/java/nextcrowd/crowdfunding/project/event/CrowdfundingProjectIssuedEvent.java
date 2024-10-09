package nextcrowd.crowdfunding.project.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;

@Value
@Builder
public class CrowdfundingProjectIssuedEvent {
    private ProjectId projectId;
}
