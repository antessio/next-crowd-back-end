package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private List<BakerId> bakers;

    public CrowdfundingProject approve(int risk, BigDecimal expectedProfit, BigDecimal minimumInvestment) {
        return this.toBuilder()
                   .risk(risk)
                   .expectedProfit(expectedProfit)
                   .minimumInvestment(minimumInvestment)
                   .status(Status.APPROVED)
                   .build();

    }

    public CrowdfundingProject reject() {
        return this.toBuilder()
                .status(Status.REJECTED)
                .build();
    }

    public CrowdfundingProject addBaker(BakerId bakerId, BigDecimal amount) {
        List<BakerId> newBakers = Optional.ofNullable(this.bakers)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);
        newBakers.add(bakerId);
        return this.toBuilder()
                .bakers(newBakers)
                .collectedAmount(this.collectedAmount.add(amount))
                .build();
    }


    public enum Status {
        SUBMITTED,
        APPROVED,
        REJECTED,
        ISSUED
    }


}
