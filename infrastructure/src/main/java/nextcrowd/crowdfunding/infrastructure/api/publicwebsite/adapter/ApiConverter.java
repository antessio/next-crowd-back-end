package nextcrowd.crowdfunding.infrastructure.api.publicwebsite.adapter;

import java.time.ZoneOffset;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
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
                ;
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
                .name(projectOwner.getName());
    }

}
