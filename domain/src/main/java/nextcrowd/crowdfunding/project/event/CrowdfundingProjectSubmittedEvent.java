package nextcrowd.crowdfunding.project.event;

import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

@Value
@Builder
public class CrowdfundingProjectSubmittedEvent {
    private ProjectId projectId;
    private ProjectOwner projectOwner;
}
