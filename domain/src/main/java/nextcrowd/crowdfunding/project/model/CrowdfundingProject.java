package nextcrowd.crowdfunding.project.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public CrowdfundingProject addBaker(Investment investment) {

        Map<BakerId, Investment> investmentsByBaker = Optional.ofNullable(this.getPendingInvestments())
                                                              .map(investmentList -> investmentList
                                                                      .stream()
                                                                      .collect(Collectors.toMap(Investment::getBakerId, Function.identity())))
                                                              .orElseGet(HashMap::new);
        Investment investmentToAdd = Optional.ofNullable(investmentsByBaker.get(investment.getBakerId()))
                                             .map(i -> i.add(investment.getAmount()))
                                             .orElse(investment);

        investmentsByBaker.put(investmentToAdd.getBakerId(), investmentToAdd);
        return this.toBuilder()
                   .pendingInvestments(new ArrayList<>(investmentsByBaker.values()))
                   .build();
    }

    public CrowdfundingProject rejectInvestment(BakerId bakerId) {
        return this.pendingInvestments
                .stream()
                .filter(i -> !i.getBakerId().equals(bakerId))
                .findFirst()
                .map(investment -> {
                    List<Investment> refusedInvestmentsToUpDate = new ArrayList<>(this.getRefusedInvestments());
                    refusedInvestmentsToUpDate.add(investment);
                    return this.toBuilder()
                               .pendingInvestments(this.pendingInvestments.stream().filter(i -> !i.equals(investment)).toList())
                               .refusedInvestments(refusedInvestmentsToUpDate)
                               .build();

                }).orElse(this);
    }

    public CrowdfundingProject acceptInvestment(BakerId bakerId, MoneyTransferId moneyTransferId) {
        if (hasConfirmedInvestment(bakerId)){
            return this;
        }
        return this.pendingInvestments
                .stream()
                .filter(i -> i.getBakerId().equals(bakerId))
                .findFirst()
                .map(pendingInvestment -> {
                    List<AcceptedInvestment> acceptedInvestmentsToUpDate = new ArrayList<>(this.getAcceptedInvestments());
                    AcceptedInvestment acceptedInvestment = new AcceptedInvestment(pendingInvestment.getBakerId(), pendingInvestment.getAmount(), moneyTransferId);
                    acceptedInvestmentsToUpDate.add(acceptedInvestment);
                    return this.toBuilder()
                               .pendingInvestments(this.pendingInvestments.stream().filter(i -> !i.equals(pendingInvestment)).toList())
                               .acceptedInvestments(acceptedInvestmentsToUpDate)
                               .collectedAmount(this.collectedAmount.add(acceptedInvestment.getAmount()))
                               .build();

                }).orElse(this);

    }

    public boolean hasConfirmedInvestment(BakerId bakerId) {
        return this.getAcceptedInvestments().stream().anyMatch(i -> i.getBakerId().equals(bakerId));
    }

    public CrowdfundingProject issue() {
        return this.toBuilder()
                   .status(Status.ISSUED)
                   .build();
    }


    public enum Status {
        SUBMITTED,
        APPROVED,
        REJECTED,
        ISSUED
    }


}
