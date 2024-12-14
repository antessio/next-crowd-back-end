package nextcrowd.crowdfunding.infrastructure.api.publicwebsite.adapter;

import java.math.BigDecimal;
import java.time.ZoneOffset;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectOwner;

public class ApiConverter {

    public static nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject toApi(CrowdfundingProject project) {
        return new nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject()
                .id(project.getId().id())
                .requestedAmount(project.getRequestedAmount().doubleValue())
                .currency(project.getCurrency())
                .projectStartDate(project.getProjectStartDate().atOffset(ZoneOffset.UTC))
                .projectEndDate(project.getProjectEndDate().atOffset(ZoneOffset.UTC))
                .owner(convertProjectOwner(project.getOwner()))
                .numberOfBackers(project.getNumberOfBackers().orElse(null))
                .status(project.getStatus().name())
                ;
    }

    public static nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject toApi(CrowdfundingProject project, ProjectContent projectContent) {

        return new nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject()
                .id(project.getId().id())
                .requestedAmount(project.getRequestedAmount().doubleValue())
                .currency(project.getCurrency())
                .projectStartDate(project.getProjectStartDate().atOffset(ZoneOffset.UTC))
                .projectEndDate(project.getProjectEndDate().atOffset(ZoneOffset.UTC))
                .owner(convertProjectOwner(project.getOwner()))
                .numberOfBackers(project.getNumberOfBackers().orElse(null))
                .title(projectContent.getTitle())
                .description(projectContent.getDescription())
                .longDescription(projectContent.getLongDescription())
                .imageUrl(projectContent.getImageUrl())
                .projectVideoUrl(projectContent.getProjectVideoUrl())
                .rewards(projectContent.getRewards().stream().map(ApiConverter::convertProjectReward).toList())
                .status(project.getStatus().name())
                .risk(project.getRisk().orElse(null))
                .expectedProfit(project.getExpectedProfit().map(BigDecimal::doubleValue).orElse(null))
                .minimumInvestment(project.getMinimumInvestment().map(BigDecimal::doubleValue).orElse(null))
                .bakersCount(project.getNumberOfBackers().map(BigDecimal::new).orElse(null));
    }

    private static nextcrowd.crowdfunding.websitepublic.api.model.ProjectReward convertProjectReward(nextcrowd.crowdfunding.project.model.ProjectReward projectReward) {
        return new nextcrowd.crowdfunding.websitepublic.api.model.ProjectReward()
                .name(projectReward.getName())
                .description(projectReward.getDescription())
                .imageUrl(projectReward.getImageUrl());
    }

    private static nextcrowd.crowdfunding.websitepublic.api.model.ProjectOwner convertProjectOwner(ProjectOwner projectOwner) {
        return new nextcrowd.crowdfunding.websitepublic.api.model.ProjectOwner()
                .id(projectOwner.getId())
                .name(projectOwner.getName())
                .imageUrl(projectOwner.getImageUrl());
    }

}
