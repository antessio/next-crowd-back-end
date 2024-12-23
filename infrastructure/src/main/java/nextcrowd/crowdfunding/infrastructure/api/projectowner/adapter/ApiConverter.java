package nextcrowd.crowdfunding.infrastructure.api.projectowner.adapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;

public final class ApiConverter {

    public static final ZoneOffset UTC = ZoneOffset.UTC;

    private ApiConverter() {
    }

    public static SubmitCrowdfundingProjectCommand toDomain(
            nextcrowd.crowdfunding.projectowner.api.model.SubmitCrowdfundingProjectCommand submitCrowdfundingProjectCommand,
            ProjectOwner projectOwner) {
        return SubmitCrowdfundingProjectCommand.builder()
                                               .title(submitCrowdfundingProjectCommand.getTitle())
                                               .imageUrl(convertResourceUrl(submitCrowdfundingProjectCommand.getImageUrl()))
                                               .requestedAmount(submitCrowdfundingProjectCommand.getRequestedAmount())
                                               .currency(submitCrowdfundingProjectCommand.getCurrency())
                                               .projectStartDate(submitCrowdfundingProjectCommand.getProjectStartDate().toInstant())
                                               .projectEndDate(submitCrowdfundingProjectCommand.getProjectEndDate().toInstant())
                                               .description(submitCrowdfundingProjectCommand.getDescription())
                                               .longDescription(submitCrowdfundingProjectCommand.getLongDescription())
                                               .projectVideoUrl(convertResourceUrl(submitCrowdfundingProjectCommand.getProjectVideoUrl()))
                                               .owner(projectOwner)
                                               .rewards(Optional.ofNullable(submitCrowdfundingProjectCommand.getRewards()).orElseGet(List::of)
                                                                .stream()
                                                                .map(ApiConverter::convertProjectReward)
                                                                .toList())
                                               .build();

    }

    private static String convertResourceUrl(String imageUrl) {
        return Optional.ofNullable(imageUrl)
                       .filter(Predicate.not(String::isBlank))
                       .orElse(null);
    }

    private static ProjectReward convertProjectReward(nextcrowd.crowdfunding.projectowner.api.model.ProjectReward projectReward) {
        return ProjectReward.builder()
                            .name(projectReward.getName())
                            .description(projectReward.getDescription())
                            .imageUrl(convertResourceUrl(projectReward.getImageUrl()))
                            .build();
    }

    public static nextcrowd.crowdfunding.projectowner.api.model.CrowdfundingProject toApi(CrowdfundingProject project, ProjectContent projectContent) {
        return new nextcrowd.crowdfunding.projectowner.api.model.CrowdfundingProject()
                .id(project.getId().id())
                .status(project.getStatus().name())
                .requestedAmount(project.getRequestedAmount().doubleValue())
                .collectedAmount(project.getCollectedAmount().map(BigDecimal::doubleValue).orElse(null))
                .currency(project.getCurrency())
                .projectStartDate(convertToOffsetDateTime(project.getProjectStartDate()))
                .projectEndDate(convertToOffsetDateTime(project.getProjectEndDate()))
                .minimumInvestment(project.getMinimumInvestment().map(BigDecimal::doubleValue).orElse(null))
                .expectedProfit(project.getExpectedProfit().map(BigDecimal::doubleValue).orElse(null))
                .risk(project.getRisk().orElse(null))
                .owner(projectOwnerToApi(project.getOwner()))
                .numberOfBackers(project.getNumberOfBackers().orElse(null))
                .title(Optional.ofNullable(projectContent).map(ProjectContent::getTitle).orElse(null))
                .description(Optional.ofNullable(projectContent).map(ProjectContent::getDescription).orElse(null))
                .longDescription(Optional.ofNullable(projectContent).map(ProjectContent::getLongDescription).orElse(null))
                .imageUrl(Optional.ofNullable(projectContent).map(ProjectContent::getImageUrl).orElse(null))
                .projectVideoUrl(Optional.ofNullable(projectContent).map(ProjectContent::getProjectVideoUrl).orElse(null))
                .rewards(Optional.ofNullable(projectContent).map(ProjectContent::getRewards).orElseGet(List::of)
                                 .stream()
                                 .map(ApiConverter::projectRewardToApi)
                                 .toList())
                ;

    }

    private static nextcrowd.crowdfunding.projectowner.api.model.ProjectReward projectRewardToApi(ProjectReward projectReward) {
        return new nextcrowd.crowdfunding.projectowner.api.model.ProjectReward()
                .name(projectReward.getName())
                .description(projectReward.getDescription())
                .imageUrl(projectReward.getImageUrl());
    }

    private static nextcrowd.crowdfunding.projectowner.api.model.ProjectOwner projectOwnerToApi(ProjectOwner owner) {
        return new nextcrowd.crowdfunding.projectowner.api.model.ProjectOwner()
                .id(owner.getId().id())
                .name(owner.getName())
                .imageUrl(owner.getImageUrl());
    }


    public static OffsetDateTime convertToOffsetDateTime(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, UTC);
    }
    public static EditCrowdfundingProjectCommand toDomain(nextcrowd.crowdfunding.projectowner.api.model.EditCrowdfundingProjectCommand editCrowdfundingProjectCommand) {
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
                                             .rewards(Optional.ofNullable(editCrowdfundingProjectCommand.getRewards()).orElseGet(List::of)
                                                              .stream()
                                                              .map(ApiConverter::convertProjectReward)
                                                              .toList())
                                             .build();
    }

}
