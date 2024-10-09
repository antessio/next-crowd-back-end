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
    private List<Investment> investments;

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

        Map<BakerId, Investment> investmentsByBaker = Optional.ofNullable(this.getInvestments())
                                                              .map(investmentList -> investmentList
                                                                      .stream()
                                                                      .collect(Collectors.toMap(Investment::getBakerId, Function.identity())))
                                                              .orElseGet(HashMap::new);
        Investment investmentToAdd = Optional.ofNullable(investmentsByBaker.get(investment.getBakerId()))
                                             .map(i -> i.add(investment.getAmount()))
                                             .orElse(investment);

        investmentsByBaker.put(investmentToAdd.getBakerId(), investmentToAdd);
        return this.toBuilder()
                   .investments(new ArrayList<>(investmentsByBaker.values()))
                   .collectedAmount(this.collectedAmount.add(investment.getAmount()))
                   .build();
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
