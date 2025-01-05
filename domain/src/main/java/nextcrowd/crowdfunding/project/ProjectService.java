package nextcrowd.crowdfunding.project;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nextcrowd.crowdfunding.common.TransactionalManager;
import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CreateProjectContent;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.ProjectContent;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwnerId;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.port.CmsPort;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;
import nextcrowd.crowdfunding.project.service.ProjectApprovalService;
import nextcrowd.crowdfunding.project.service.ProjectEditingService;
import nextcrowd.crowdfunding.project.service.ProjectInvestmentService;
import nextcrowd.crowdfunding.project.service.ProjectIssuingService;
import nextcrowd.crowdfunding.project.service.ProjectRejectionService;
import nextcrowd.crowdfunding.project.service.ProjectSubmissionService;
import nextcrowd.crowdfunding.project.service.ProjectValidationService;

public class ProjectService implements ProjectServicePort {

    private final ProjectValidationService validationService;
    private final CrowdfundingProjectRepository repository;
    private final ProjectSubmissionService projectSubmissionService;
    private final ProjectApprovalService projectApprovalService;
    private final ProjectRejectionService projectRejectionService;
    private final ProjectInvestmentService projectInvestment;
    private final ProjectIssuingService projectIssuingService;
    private final TransactionalManager transactionalManager;
    private static final Set<CrowdfundingProject.Status> PUBLISHED_STATUSES = Set.of(CrowdfundingProject.Status.APPROVED, CrowdfundingProject.Status.COMPLETED);
    private final ProjectEditingService projectEditingService;
    private final CmsPort cms;

    public ProjectService(
            ProjectValidationService validationService,
            CrowdfundingProjectRepository repository,
            EventPublisher eventPublisher,
            CmsPort cms,
            TransactionalManager transactionalManager) {
        this.validationService = validationService;
        this.repository = repository;
        this.transactionalManager = transactionalManager;
        this.cms = cms;
        this.projectSubmissionService = new ProjectSubmissionService(eventPublisher, repository);
        this.projectApprovalService = new ProjectApprovalService(eventPublisher, repository);
        this.projectRejectionService = new ProjectRejectionService(eventPublisher, repository);
        this.projectInvestment = new ProjectInvestmentService(eventPublisher, repository);
        this.projectIssuingService = new ProjectIssuingService(eventPublisher, repository);
        this.projectEditingService = new ProjectEditingService(repository);
    }

    @Override
    public Optional<ProjectContent> getContentById(ProjectId projectId) {
        return cms.getProjectContent(projectId);
    }

    @Override
    public Optional<CrowdfundingProject> getById(ProjectId projectId) {
        return repository.findById(projectId);
    }

    @Override
    public Stream<CrowdfundingProject> getPublishedProjects(ProjectId startingFrom) {
        return repository.findByStatusesOrderByAsc(PUBLISHED_STATUSES, startingFrom);
    }

    @Override
    public Stream<CrowdfundingProject> getPendingReviewProjects(ProjectId startingFrom) {
        return repository.findByStatusesOrderByAsc(Set.of(CrowdfundingProject.Status.SUBMITTED), startingFrom);
    }

    @Override
    public Stream<CrowdfundingProject> getProjectsByProjectOwnerId(ProjectOwnerId projectOwnerId, ProjectId startingFrom) {
        return repository.findByOwnerIdOrderByAsc(projectOwnerId, startingFrom);
    }

    @Override
    public Stream<Investment> getPendingInvestments(ProjectId projectId, InvestmentId startingFrom) {
        return repository.findInvestmentsByStatusesOrderByDesc(projectId, startingFrom, Set.of(InvestmentStatus.PENDING));
    }

    @Override
    public Stream<Investment> getAcceptedInvestments(ProjectId projectId, InvestmentId startingFrom) {
        return repository.findInvestmentsByStatusesOrderByDesc(projectId, startingFrom, Set.of(InvestmentStatus.ACCEPTED));
    }

    @Override
    public ProjectId submitProject(SubmitCrowdfundingProjectCommand projectCreationCommand) {
        List<ProjectValidationService.ValidationFailure> failedValidations = validationService.validateProjectSubmission(projectCreationCommand);
        if (!failedValidations.isEmpty()) {
            throw new ValidationException(
                    failedValidations.stream().map(ProjectValidationService.ValidationFailure::reason)
                                     .collect(Collectors.joining("\n")));
        }
        CrowdfundingProject project = transactionalManager.executeInTransaction(() -> {
            CrowdfundingProject p = projectSubmissionService.submit(projectCreationCommand);
            CreateProjectContent projectContent = CreateProjectContent.builder()
                                                                .currency(projectCreationCommand.getCurrency())
                                                                .owner(projectCreationCommand.getOwner())
                                                                .requestedAmount(BigDecimal.valueOf(projectCreationCommand.getRequestedAmount()))
                                                                .projectStartDate(projectCreationCommand.getProjectStartDate())
                                                                .projectEndDate(projectCreationCommand.getProjectEndDate())
                                                                .longDescription(projectCreationCommand.getLongDescription())
                                                                .description(projectCreationCommand.getDescription())
                                                                .rewards(projectCreationCommand.getRewards())
                                                                .video(projectCreationCommand.getVideo())
                                                                .title(projectCreationCommand.getTitle())
                                                                .image(projectCreationCommand.getImage())
                                                                .projectId(p.getId())
                                                                .build();
            cms.saveContent(projectContent);
            return p;
        });
        // TODO: async on ProjectSubmittedEvent

        return project.getId();
    }

    @Override
    public void editProject(ProjectId projectId, EditCrowdfundingProjectCommand editCrowdfundingProjectCommand) {
        List<ProjectValidationService.ValidationFailure> failedValidations = validationService.validateProjectEdit(editCrowdfundingProjectCommand);
        if (!failedValidations.isEmpty()) {
            throw new ValidationException(
                    failedValidations.stream().map(ProjectValidationService.ValidationFailure::reason)
                                     .collect(Collectors.joining("\n")));
        }
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(() -> projectEditingService.edit(editCrowdfundingProjectCommand, project));
    }

    @Override
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

    @Override
    public void reject(ProjectId projectId) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));

        transactionalManager.executeInTransaction(() -> projectRejectionService.reject(project));

    }

    @Override
    public void addInvestment(ProjectId projectId, AddInvestmentCommand command) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(() -> projectInvestment.addInvestment(command, project));
    }

    @Override
    public void confirmInvestment(ProjectId projectId, ConfirmInvestmentCommand command) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(() -> projectInvestment.confirmInvestment(command, project));

    }

    @Override
    public void cancelInvestment(ProjectId projectId, CancelInvestmentCommand command) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));

        transactionalManager.executeInTransaction(() -> projectInvestment.cancelInvestment(command, project));
    }

    @Override
    public void issue(ProjectId projectId) {
        CrowdfundingProject project = repository.findById(projectId)
                                                .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));
        transactionalManager.executeInTransaction(() -> projectIssuingService.issue(project));
    }


}
