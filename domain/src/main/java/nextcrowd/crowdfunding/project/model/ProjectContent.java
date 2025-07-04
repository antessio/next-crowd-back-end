package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ProjectContent {

    private String currency;
    private BigDecimal collectedAmount;
    private ProjectOwner owner;
    private String title;
    private UploadedResource image;
    private BigDecimal requestedAmount;
    private String description;
    private String longDescription;
    private List<ProjectReward> rewards;
    private UploadedResource video;
    private Instant projectStartDate;
    private Instant projectEndDate;
    private Integer numberOfBackers;
    private Integer risk;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;
    private ProjectId projectId;

    @Value
    @Builder
    public static class ProjectOwner {

        private String id;
        private String name;
        private UploadedResource image;

    }

}
