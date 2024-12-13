package nextcrowd.crowdfunding.project.service;

import java.math.BigDecimal;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;

public class ProjectEditingService {

    private final CrowdfundingProjectRepository repository;

    public ProjectEditingService(CrowdfundingProjectRepository repository) {
        this.repository = repository;
    }

    public CrowdfundingProject edit(EditCrowdfundingProjectCommand projectCreationCommand, CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.SUBMITTED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
        CrowdfundingProject editedProject = project.toBuilder()
                                                   .owner(projectCreationCommand.getOwner())
                                                   .currency(projectCreationCommand.getCurrency())
                                                   .requestedAmount(BigDecimal.valueOf(projectCreationCommand.getRequestedAmount()))
                                                   .projectStartDate(projectCreationCommand.getProjectStartDate())
                                                   .projectEndDate(projectCreationCommand.getProjectEndDate())
                                                   .status(project.isReadyToBeSubmitted() ? CrowdfundingProject.Status.SUBMITTED : project.getStatus())
                                                   .build();

        return repository.save(editedProject);
    }
}
