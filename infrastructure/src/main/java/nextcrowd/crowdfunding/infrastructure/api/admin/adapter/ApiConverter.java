package nextcrowd.crowdfunding.infrastructure.api.admin.adapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import nextcrowd.crowdfunding.admin.api.model.ProjectTimeline;
import nextcrowd.crowdfunding.admin.api.model.ProjectTimelineEventsInner;
import nextcrowd.crowdfunding.admin.api.model.UpdateTimelineCommand;
import nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.TimelineEventCommand;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.model.Timeline;
import nextcrowd.crowdfunding.project.model.TimelineEvent;

public final class ApiConverter {

    public static final ZoneOffset UTC = ZoneOffset.UTC;

    private ApiConverter() {
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
                                                                .map(ApiConverter::convertProjectReward)
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
                                       .moneyTransferId(new MoneyTransferId(confirmInvestmentCommand.getMoneyTransferId()))
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
                .projectVideoUrl(project.getProjectVideoUrl())
                .minimumInvestment(project.getMinimumInvestment().map(BigDecimal::doubleValue).orElse(null))
                .expectedProfit(project.getExpectedProfit().map(BigDecimal::doubleValue).orElse(null))
                .risk(project.getRisk().orElse(null))
                .owner(projectOwnerToApi(project.getOwner()))
                .numberOfBackers(project.getNumberOfBackers().orElse(null))
                .rewards(project.getRewards().stream().map(ApiConverter::projectRewardToApi).toList());
    }

    private static nextcrowd.crowdfunding.admin.api.model.ProjectReward projectRewardToApi(ProjectReward projectReward) {
        return new nextcrowd.crowdfunding.admin.api.model.ProjectReward()
                .name(projectReward.getName())
                .description(projectReward.getDescription())
                .imageUrl(projectReward.getImageUrl());
    }

    private static nextcrowd.crowdfunding.admin.api.model.ProjectOwner projectOwnerToApi(ProjectOwner owner) {
        return new nextcrowd.crowdfunding.admin.api.model.ProjectOwner()
                .id(owner.getId())
                .name(owner.getName())
                .imageUrl(owner.getImageUrl());
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

    public static EditCrowdfundingProjectCommand toDomain(nextcrowd.crowdfunding.admin.api.model.EditCrowdfundingProjectCommand editCrowdfundingProjectCommand) {
        return EditCrowdfundingProjectCommand.builder()
                                             .title(editCrowdfundingProjectCommand.getTitle())
                                             .imageUrl(editCrowdfundingProjectCommand.getImageUrl())
                                             .requestedAmount(editCrowdfundingProjectCommand.getRequestedAmount())
                                             .currency(editCrowdfundingProjectCommand.getCurrency())
                                             .projectStartDate(editCrowdfundingProjectCommand.getProjectStartDate().toInstant())
                                             .projectEndDate(editCrowdfundingProjectCommand.getProjectEndDate().toInstant())
                                             .description(editCrowdfundingProjectCommand.getDescription())
                                             .longDescription(editCrowdfundingProjectCommand.getLongDescription())
                                             .projectVideoUrl(editCrowdfundingProjectCommand.getProjectVideoUrl())
                                             .owner(convertProjectOwner(editCrowdfundingProjectCommand.getOwner()))
                                             .rewards(Optional.ofNullable(editCrowdfundingProjectCommand.getRewards()).orElseGet(List::of)
                                                              .stream()
                                                              .map(ApiConverter::convertProjectReward)
                                                              .toList())
                                             .build();
    }

    public static ProjectTimeline toApi(Timeline timeline) {
        return new ProjectTimeline()
                .events(timeline.getEvents().stream().map(ApiConverter::toApi).toList());
    }

    private static ProjectTimelineEventsInner toApi(TimelineEvent timelineEvent) {
        return new ProjectTimelineEventsInner()
                .date(convertToOffsetDateTime(timelineEvent.getDate()))
                .description(timelineEvent.getDescription())
                .title(timelineEvent.getTitle());
    }

    private static OffsetDateTime convertToOffsetDateTime(LocalDate localDate) {
        return localDate == null ? null : OffsetDateTime.of(localDate, Instant.EPOCH.atZone(UTC).toLocalTime(), UTC);
    }

    public static List<TimelineEventCommand> toDomain(UpdateTimelineCommand updateTimelineCommand) {
        return updateTimelineCommand.getEvents()
                                    .stream()
                                    .map(e -> new TimelineEventCommand(
                                            null,
                                            e.getTitle(),
                                            e.getDescription(),
                                            e.getDate().toLocalDate()))
                                    .toList();

    }

}
