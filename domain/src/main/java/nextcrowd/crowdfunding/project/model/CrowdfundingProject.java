package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CrowdfundingProject {
    private ProjectId id;
    private String title;
    private BigDecimal requestedAmount;
    private BigDecimal collectedAmount;
    private String currency;
    private String imageUrl;
    private int risk;
    private ProjectOwner owner;
    private Instant projectStartDate;
    private Instant projectEndDate;
    private int numberOfBackers;
    private String description;
    private String longDescription;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;
    private List<ProjectReward> rewards;
    private String projectVideoUrl;


}
