package nextcrowd.crowdfunding.project.command;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import nextcrowd.crowdfunding.project.model.UploadedResource;

@Getter
@Builder
public class SubmitCrowdfundingProjectCommand {
    private String title;
    private UploadedResource image;
    private double requestedAmount;
    private String currency;
    private ProjectOwner owner;
    private Instant projectStartDate;
    private Instant projectEndDate;
    private String description;
    private String longDescription;
    private List<ProjectReward> rewards;
    private UploadedResource video;


    @Getter
    @Builder
    public static class ProjectReward{
        private String name;
        private String description;
        private UploadedResource image;
    }


    @Getter
    @Builder
    public static class ProjectOwner{
        private String id;
        private String name;
        private UploadedResource image;
    }

}

