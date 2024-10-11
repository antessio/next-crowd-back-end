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
    private List<Investment> pendingInvestments;
    private List<AcceptedInvestment> acceptedInvestments;
    private List<Investment> refusedInvestments;


    public boolean hasConfirmedInvestment(BakerId bakerId) {
        return this.getAcceptedInvestments().stream().anyMatch(i -> i.getBakerId().equals(bakerId));
    }
    public boolean hasCanceledInvestment(BakerId bakerId) {
        return this.getRefusedInvestments().stream().anyMatch(i -> i.getBakerId().equals(bakerId));
    }


    public enum Status {
        SUBMITTED,
        APPROVED,
        REJECTED,
        ISSUED
    }


}
