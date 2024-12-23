package nextcrowd.crowdfunding.project.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectOwnerId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;

public class ProjectSubmissionService {

    private final EventPublisher eventPublisher;
    private final CrowdfundingProjectRepository repository;

    public ProjectSubmissionService(EventPublisher eventPublisher, CrowdfundingProjectRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    public CrowdfundingProject submit(SubmitCrowdfundingProjectCommand projectCreationCommand) {
        ProjectOwner owner = getOrCreateProjectOwner(projectCreationCommand.getOwner());
        CrowdfundingProject project = CrowdfundingProject.builder()
                                                         .id(generateId())
                                                         .owner(owner)
                                                         .projectStartDate(projectCreationCommand.getProjectStartDate())
                                                         .projectEndDate(projectCreationCommand.getProjectEndDate())
                                                         .requestedAmount(BigDecimal.valueOf(projectCreationCommand.getRequestedAmount()))
                                                         .currency(projectCreationCommand.getCurrency())
                                                         .status(CrowdfundingProject.Status.SUBMITTED)
                                                         .build();
        repository.save(project);
        eventPublisher.publish(CrowdfundingProjectSubmittedEvent.builder()
                                                                .projectOwner(project.getOwner())
                                                                .projectId(project.getId())
                                                                .build());
        return project;
    }

    private ProjectOwner getOrCreateProjectOwner(ProjectOwner owner) {
        return Optional.ofNullable(owner.getId())
                       .flatMap(repository::findOwnerById)
                       .orElseGet(() -> repository.createProjectOwner(ProjectOwner.builder()
                                                                                  .id(Optional.ofNullable(owner.getId())
                                                                                              .orElseGet(() -> new ProjectOwnerId(UuidCreator.getTimeOrdered()
                                                                                                                                             .toString())))
                                                                                  .name(owner.getName())
                                                                                  .imageUrl(owner.getImageUrl())
                                                                                  .build()));

    }

    private ProjectId generateId() {
        return new ProjectId(UuidCreator.getTimeOrderedEpoch().toString());
    }

}
