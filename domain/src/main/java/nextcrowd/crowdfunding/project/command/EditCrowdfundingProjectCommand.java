package nextcrowd.crowdfunding.project.command;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;

@Getter
@Builder
public class EditCrowdfundingProjectCommand {
    private String title;
    private String imageUrl;
    private double requestedAmount;
    private String currency;
    private ProjectOwner owner;
    private Instant projectStartDate;
    private Instant projectEndDate;
    private String description;
    private String longDescription;
    private List<ProjectReward> rewards;
    private String projectVideoUrl;

}

