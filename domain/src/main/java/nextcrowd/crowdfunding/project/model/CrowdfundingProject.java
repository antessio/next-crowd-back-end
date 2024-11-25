package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;
import java.time.Instant;
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
    private Integer  numberOfBackers;
    private String description;
    private String longDescription;
    private List<ProjectReward> rewards;
    private String projectVideoUrl;
    private Integer risk;
    private BigDecimal expectedProfit;
    private BigDecimal minimumInvestment;
    private List<Investment> investments;

    public Optional<BigDecimal> getMinimumInvestment() {
        return Optional.ofNullable(minimumInvestment);
    }

    public Optional<BigDecimal> getExpectedProfit() {
        return Optional.ofNullable(expectedProfit);
    }

    public Optional<Integer> getRisk() {
        return Optional.ofNullable(risk);
    }

    public List<Investment> getAcceptedInvestments() {
        return investments.stream().filter(Investment::isAccepted).toList();
    }

    public List<Investment> getRefusedInvestments() {
        return investments.stream().filter(Investment::isRefused).toList();
    }

    public List<Investment> getPendingInvestments() {
        return investments.stream().filter(Investment::isPending).toList();
    }

    public Optional<BigDecimal> getCollectedAmount(){
        return Optional.ofNullable(collectedAmount);
    }


    public boolean hasConfirmedInvestment(BakerId bakerId) {
        return this.getAcceptedInvestments().stream().anyMatch(i -> i.getBakerId().equals(bakerId));
    }

    public boolean hasCanceledInvestment(BakerId bakerId) {
        return this.getRefusedInvestments().stream().anyMatch(i -> i.getBakerId().equals(bakerId));
    }

    public CrowdfundingProject updateInvestments(List<Investment> updatedInvestments) {
        return this.toBuilder()
                   .investments(updatedInvestments)
                   .collectedAmount(updatedInvestments.stream()
                                                      .filter(Investment::isAccepted)
                                                      .map(Investment::getAmount)
                                                      .reduce(BigDecimal.ZERO, BigDecimal::add))
                   .build();
    }


    public enum Status {
        SUBMITTED,
        APPROVED,
        REJECTED,
        COMPLETED
    }


}
