package nextcrowd.crowdfunding.infrastructure.api.publicwebsite.adapter;

import java.math.BigDecimal;
import java.net.URI;
import java.time.ZoneOffset;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.UploadedResource;
import nextcrowd.crowdfunding.websitepublic.api.model.FileUploadResponse;

public class ApiConverter {

    public static nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject toApi(CrowdfundingProject project) {
        return new nextcrowd.crowdfunding.websitepublic.api.model.CrowdfundingProject()
                .id(project.getId().id())
                .requestedAmount(project.getRequestedAmount().doubleValue())
                .currency(project.getCurrency())
                .projectStartDate(project.getProjectStartDate().atOffset(ZoneOffset.UTC))
                .projectEndDate(project.getProjectEndDate().atOffset(ZoneOffset.UTC))
                .owner(convertProjectOwner(project.getOwner(), null))
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
                .owner(convertProjectOwner(project.getOwner(),
                                           Optional.ofNullable(projectContent.getOwner())
                                                   .orElse(null)))
                .numberOfBackers(project.getNumberOfBackers().orElse(null))
                .title(projectContent.getTitle())
                .description(projectContent.getDescription())
                .longDescription(projectContent.getLongDescription())
                .image(convertUploadResourceToApi(projectContent.getImage()))
                .projectVideo(convertUploadResourceToApi(projectContent.getVideo()))
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
                .image(convertUploadResourceToApi(projectReward.getImage()));
    }

    private static nextcrowd.crowdfunding.websitepublic.api.model.ProjectOwner convertProjectOwner(ProjectOwner projectOwner, ProjectContent.ProjectOwner projectOwnerContent) {
        return new nextcrowd.crowdfunding.websitepublic.api.model.ProjectOwner()
                .id(projectOwner.getId().id())
                .name(projectOwner.getName())
                .image(Optional.ofNullable(projectOwnerContent).map(ProjectContent.ProjectOwner::getImage)
                               .map(ApiConverter::convertUploadResourceToApi)
                               .orElse(null));
    }

    private static FileUploadResponse convertUploadResourceToApi(@NotNull UploadedResource resource) {
        return Optional.ofNullable(resource)
                       .map(r -> {
                           FileUploadResponse uploadedResource
                                   = new FileUploadResponse();
                           uploadedResource.path(r.getPath());
                           uploadedResource.contentType(r.getContentType());
                           uploadedResource.location(r.getLocation().name());
                           uploadedResource.url(URI.create(r.getUrl()));
                           uploadedResource.id(r.getId().id());
                           return uploadedResource;
                       })
                       .orElse(null);
    }

}
