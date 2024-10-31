package nextcrowd.crowdfunding.project;

import java.util.List;
import java.util.stream.Collectors;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;
import nextcrowd.crowdfunding.project.port.TransactionalManager;
import nextcrowd.crowdfunding.project.service.ProjectApprovalService;
import nextcrowd.crowdfunding.project.service.ProjectInvestmentService;
import nextcrowd.crowdfunding.project.service.ProjectIssuingService;
import nextcrowd.crowdfunding.project.service.ProjectRejectionService;
import nextcrowd.crowdfunding.project.service.ProjectSubmissionService;
import nextcrowd.crowdfunding.project.service.ProjectValidationService;

public class ProjectService {

    private final ProjectValidationService validationService;
    private final CrowdfundingProjectRepository repository;
    private final ProjectSubmissionService projectSubmissionService;
    private final ProjectApprovalService projectApprovalService;
    private final ProjectRejectionService projectRejectionService;
    private final ProjectInvestmentService projectInvestment;
    private final ProjectIssuingService projectIssuingService;
    private final TransactionalManager transactionalManager;

    public ProjectService(
            ProjectValidationService validationService,
            CrowdfundingProjectRepository repository,
            EventPublisher eventPublisher,
            TransactionalManager transactionalManager) {
        this.validationService = validationService;
        this.repository = repository;
        this.transactionalManager = transactionalManager;
        this.projectSubmissionService = new ProjectSubmissionService(eventPublisher, repository);
        this.projectApprovalService = new ProjectApprovalService(eventPublisher, repository);
        this.projectRejectionService = new ProjectRejectionService(eventPublisher, repository);
        this.projectInvestment = new ProjectInvestmentService(eventPublisher, repository);
        this.projectIssuingService = new ProjectIssuingService(eventPublisher, repository);
    }

    public ProjectId submitProject(SubmitCrowdfundingProjectCommand projectCreationCommand) {
        List<ProjectValidationService.ValidationFailure> failedValidations = validationService.validateProjectSubmission(projectCreationCommand);
        if (!failedValidations.isEmpty()) {
            throw new ValidationException(
                    failedValidations.stream().map(ProjectValidationService.ValidationFailure::reason)
                                     .collect(Collectors.joining("\n")));
        }
        CrowdfundingProject project = transactionalManager.executeInTransaction(() -> projectSubmissionService.submit(projectCreationCommand));
        return project.getId();
    }

    public void approve(ProjectId projectId, ApproveCrowdfundingProjectCommand command) {
        List<ProjectValidationService.ValidationFailure> failedValidations = validationService.validateProjectApproval(command);
        if (!failedValidations.isEmpty()) {
            throw new CrowdfundingProjectException(
                    failedValidations.stream().map(ProjectValidationService.ValidationFailure::reason)
                                     .collect(Collectors.joining("\n")),
                    CrowdfundingProjectException.Reason.INVALID_COMMAND);
        }
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));

        transactionalManager.executeInTransaction(() -> projectApprovalService.approve(command, project));
    }

    public void reject(ProjectId projectId) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));

        transactionalManager.executeInTransaction(() -> projectRejectionService.reject(project));

    }

    public void addInvestment(ProjectId projectId, AddInvestmentCommand command) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(() -> projectInvestment.addInvestment(command, project));
    }

    public void confirmInvestment(ProjectId projectId, ConfirmInvestmentCommand command) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(() -> projectInvestment.confirmInvestment(command, project));

    }

    public void cancelInvestment(ProjectId projectId, CancelInvestmentCommand command) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(()->projectInvestment.cancelInvestment(command, project));
    }

    public void issue(ProjectId projectId) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(()->projectIssuingService.issue(project));
    }


}
