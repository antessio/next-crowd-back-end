package nextcrowd.crowdfunding.project;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.exception.ProjectApprovalException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;
import nextcrowd.crowdfunding.project.service.ProjectValidationService;

public class ProjectService {

    private final ProjectValidationService validationService;
    private final CrowdfundingProjectRepository crowdfundingProjectRepository;
    private final EventPublisher eventPublisher;

    public ProjectService(
            ProjectValidationService validationService,
            CrowdfundingProjectRepository crowdfundingProjectRepository,
            EventPublisher eventPublisher) {
        this.validationService = validationService;
        this.crowdfundingProjectRepository = crowdfundingProjectRepository;
        this.eventPublisher = eventPublisher;
    }

    public ProjectId submitProject(SubmitCrowdfundingProjectCommand projectCreationCommand) {
        List<ProjectValidationService.ValidationFailure> failedValidations = validationService.validateProjectSubmission(projectCreationCommand);
        if (!failedValidations.isEmpty()) {
            throw new ValidationException(
                    failedValidations.stream().map(ProjectValidationService.ValidationFailure::reason)
                                     .collect(Collectors.joining("\n")));
        }
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
        crowdfundingProjectRepository.save(project);
        eventPublisher.publish(CrowdfundingProjectSubmittedEvent.builder()
                                                                .projectOwner(project.getOwner())
                                                                .projectId(project.getId())
                                                                .build());

        return project.getId();
    }

    public void approve(ProjectId projectId, ApproveCrowdfundingProjectCommand command) {
        CrowdfundingProject project = crowdfundingProjectRepository.findById(projectId)
                                                                   .orElseThrow(() -> new ProjectApprovalException(ProjectApprovalException.Reason.PROJECT_NOT_FOUND));
        if (project.getStatus() == CrowdfundingProject.Status.APPROVED) {
            return;
        }
        if (project.getStatus() != CrowdfundingProject.Status.SUBMITTED) {
            throw new ProjectApprovalException(ProjectApprovalException.Reason.INVALID_PROJECT_STATUS);
        }
        List<ProjectValidationService.ValidationFailure> failedValidations = validationService.validateProjectApproval(command);
        if (!failedValidations.isEmpty()) {
            throw new ProjectApprovalException(
                    failedValidations.stream().map(ProjectValidationService.ValidationFailure::reason)
                                     .collect(Collectors.joining("\n")),
                    ProjectApprovalException.Reason.INVALID_COMMAND);
        }

        CrowdfundingProject approved = project.approve(command.getRisk(), command.getExpectedProfit(), command.getMinimumInvestment());
        crowdfundingProjectRepository.save(approved);
        eventPublisher.publish(CrowdfundingProjectApprovedEvent.builder()
                                                               .projectId(approved.getId())
                                                               .minimumInvestment(approved.getMinimumInvestment())
                                                               .risk(approved.getRisk())
                                                               .expectedProfit(approved.getExpectedProfit())
                                                               .build());
    }

    public void reject(ProjectId projectId){
        CrowdfundingProject project = crowdfundingProjectRepository.findById(projectId)
                                                                   .orElseThrow(() -> new ProjectApprovalException(ProjectApprovalException.Reason.PROJECT_NOT_FOUND));
        if (project.getStatus() == CrowdfundingProject.Status.REJECTED) {
            return;
        }
        if (project.getStatus() != CrowdfundingProject.Status.SUBMITTED) {
            throw new ProjectApprovalException(ProjectApprovalException.Reason.INVALID_PROJECT_STATUS);
        }
        CrowdfundingProject rejected = project.reject();
        crowdfundingProjectRepository.save(rejected);
        eventPublisher.publish(CrowdfundingProjectRejectedEvent.builder()
                                                               .projectId(rejected.getId())
                                                               .build());

    }

    private ProjectId generateId() {
        return new ProjectId(UUID.randomUUID().toString());
    }

}
