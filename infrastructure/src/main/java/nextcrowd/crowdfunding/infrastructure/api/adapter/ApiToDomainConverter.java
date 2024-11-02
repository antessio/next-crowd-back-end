package nextcrowd.crowdfunding.infrastructure.api.adapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;

public final class ApiToDomainConverter {

    public static final ZoneOffset UTC = ZoneOffset.UTC;

    private ApiToDomainConverter() {
    }

    public static SubmitCrowdfundingProjectCommand toDomain(nextcrowd.crowdfunding.admin.api.model.SubmitCrowdfundingProjectCommand submitCrowdfundingProjectCommand) {
        return SubmitCrowdfundingProjectCommand.builder()
                                               .title(submitCrowdfundingProjectCommand.getTitle())
                                               .imageUrl(submitCrowdfundingProjectCommand.getImageUrl())
                                               .requestedAmount(submitCrowdfundingProjectCommand.getRequestedAmount())
                                               .currency(submitCrowdfundingProjectCommand.getCurrency())
                                               .projectStartDate(submitCrowdfundingProjectCommand.getProjectStartDate().toInstant())
                                               .projectEndDate(submitCrowdfundingProjectCommand.getProjectEndDate().toInstant())
                                               .description(submitCrowdfundingProjectCommand.getDescription())
                                               .longDescription(submitCrowdfundingProjectCommand.getLongDescription())
                                               .projectVideoUrl(submitCrowdfundingProjectCommand.getProjectVideoUrl())
                                               .owner(convertProjectOwner(submitCrowdfundingProjectCommand.getOwner()))
                                               .rewards(Optional.ofNullable(submitCrowdfundingProjectCommand.getRewards()).orElseGet(List::of)
                                                                .stream()
                                                                .map(ApiToDomainConverter::convertProjectReward)
                                                                .toList())
                                               .build();

    }

    private static ProjectReward convertProjectReward(nextcrowd.crowdfunding.admin.api.model.ProjectReward projectReward) {
        return ProjectReward.builder()
                            .name(projectReward.getName())
                            .description(projectReward.getDescription())
                            .imageUrl(projectReward.getImageUrl())
                            .build();
    }

    private static ProjectOwner convertProjectOwner(nextcrowd.crowdfunding.admin.api.model.ProjectOwner owner) {
        return ProjectOwner.builder()
                           .id(owner.getId())
                           .name(owner.getName())
                           .imageUrl(owner.getImageUrl())
                           .build();
    }

    public static ApproveCrowdfundingProjectCommand toDomain(nextcrowd.crowdfunding.admin.api.model.ApproveCrowdfundingProjectCommand approveCrowdfundingProjectCommand) {
        return ApproveCrowdfundingProjectCommand.builder()
                                                .risk(approveCrowdfundingProjectCommand.getRisk())
                                                .expectedProfit(convertBigDecimal(approveCrowdfundingProjectCommand.getExpectedProfit()))
                                                .minimumInvestment(convertBigDecimal(approveCrowdfundingProjectCommand.getMinimumInvestment()))
                                                .build();
    }

    public static CancelInvestmentCommand toDomain(nextcrowd.crowdfunding.admin.api.model.CancelInvestmentCommand cancelInvestmentCommand) {
        return CancelInvestmentCommand.builder()
                                      .bakerId(new BakerId(cancelInvestmentCommand.getBakerId()))
                                      .build();
    }

    public static ConfirmInvestmentCommand toDomain(nextcrowd.crowdfunding.admin.api.model.ConfirmInvestmentCommand confirmInvestmentCommand) {
        return ConfirmInvestmentCommand.builder()
                                       .bakerId(new BakerId(confirmInvestmentCommand.getBakerId()))
                                       .build();
    }

    public static AddInvestmentCommand toDomain(nextcrowd.crowdfunding.admin.api.model.AddInvestmentCommand addInvestmentCommand) {
        return AddInvestmentCommand.builder()
                                   .amount(convertBigDecimal(addInvestmentCommand.getAmount()))
                                   .bakerId(new BakerId(addInvestmentCommand.getBakerId()))
                                   .build();
    }

    public static nextcrowd.crowdfunding.admin.api.model.CrowdfundingProject toApi(CrowdfundingProject project) {
        return new nextcrowd.crowdfunding.admin.api.model.CrowdfundingProject()
                .id(project.getId().id())
                .title(project.getTitle())
                .status(project.getStatus().name())
                .imageUrl(project.getImageUrl())
                .requestedAmount(project.getRequestedAmount().doubleValue())
                .collectedAmount(project.getCollectedAmount().map(BigDecimal::doubleValue).orElse(null))
                .currency(project.getCurrency())
                .projectStartDate(convertToOffsetDateTime(project.getProjectStartDate()))
                .projectEndDate(convertToOffsetDateTime(project.getProjectEndDate()))
                .description(project.getDescription())
                .longDescription(project.getLongDescription())
                .projectVideoUrl(project.getProjectVideoUrl());
    }

    public static nextcrowd.crowdfunding.admin.api.model.Investment toApi(Investment investment) {
        return new nextcrowd.crowdfunding.admin.api.model.Investment()
                .amount(investment.getAmount().doubleValue())
                .id(investment.getId().id())
                .status(nextcrowd.crowdfunding.admin.api.model.Investment.StatusEnum.fromValue(investment.getStatus().name()))
                .bakerId(investment.getBakerId().id())
                .moneyTransferId(investment.getMoneyTransferId().map(MoneyTransferId::id).orElse(null));
    }

    public static OffsetDateTime convertToOffsetDateTime(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, UTC);
    }

    private static BigDecimal convertBigDecimal(Double approveCrowdfundingProjectCommand) {
        return BigDecimal.valueOf(approveCrowdfundingProjectCommand);
    }

}
