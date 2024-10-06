package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CrowdfundingProject {

    private ProjectId id;
    private String title;
    private Status status;
    private BigDecimal requestedAmount;
    private BigDecimal collectedAmount;
    private String currency;
    private String imageUrl;
    private ProjectOwner owner;
    private Instant projectStartDate;
    private Instant projectEndDate;
    private int numberOfBackers;
    private String description;
    private String longDescription;
    private List<ProjectReward> rewards;
    private String projectVideoUrl;
    private int risk;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;

    public CrowdfundingProject approve(int risk, BigDecimal expectedProfit, BigDecimal minimumInvestment) {
        return this.toBuilder()
                   .risk(risk)
                   .expectedProfit(expectedProfit)
                   .minimumInvestment(minimumInvestment)
                   .status(Status.APPROVED)
                   .build();

    }


    public enum Status {
        SUBMITTED,
        APPROVED,
        REJECTED,
        ISSUED
    }


}
