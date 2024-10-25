package nextcrowd.crowdfunding.project.service;

import java.math.BigDecimal;
import java.util.UUID;

import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;
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
        CrowdfundingProject project = CrowdfundingProject.builder()
                                                         .id(generateId())
                                                         .owner(projectCreationCommand.getOwner())
                                                         .projectStartDate(projectCreationCommand.getProjectStartDate())
                                                         .projectEndDate(projectCreationCommand.getProjectEndDate())
                                                         .projectVideoUrl(projectCreationCommand.getProjectVideoUrl())
                                                         .requestedAmount(BigDecimal.valueOf(projectCreationCommand.getRequestedAmount()))
                                                         .currency(projectCreationCommand.getCurrency())
                                                         .description(projectCreationCommand.getDescription())
                                                         .title(projectCreationCommand.getTitle())
                                                         .longDescription(projectCreationCommand.getLongDescription())
                                                         .rewards(projectCreationCommand.getRewards())
                                                         .build();
        repository.save(project);
        eventPublisher.publish(CrowdfundingProjectSubmittedEvent.builder()
                                                                               .projectOwner(project.getOwner())
                                                                               .projectId(project.getId())
                                                                               .build());
        return project;
    }

    private ProjectId generateId() {
        return new ProjectId(UUID.randomUUID().toString());
    }

}
