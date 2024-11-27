package nextcrowd.crowdfunding.project;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.EditCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.TimelineEventCommand;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.Timeline;
import nextcrowd.crowdfunding.project.model.TimelineEvent;
import nextcrowd.crowdfunding.project.model.TimelineEventId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;
import nextcrowd.crowdfunding.project.port.TransactionalManager;
import nextcrowd.crowdfunding.project.service.ProjectApprovalService;
import nextcrowd.crowdfunding.project.service.ProjectEditingService;
import nextcrowd.crowdfunding.project.service.ProjectInvestmentService;
import nextcrowd.crowdfunding.project.service.ProjectIssuingService;
import nextcrowd.crowdfunding.project.service.ProjectRejectionService;
import nextcrowd.crowdfunding.project.service.ProjectSubmissionService;
import nextcrowd.crowdfunding.project.service.ProjectTimelineService;
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
    private final ProjectTimelineService projectTimelineService;

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
        this.projectEditingService = new ProjectEditingService(repository);
        this.projectTimelineService = new ProjectTimelineService(repository);
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
        CrowdfundingProject project = transactionalManager.executeInTransaction(() -> projectSubmissionService.submit(projectCreationCommand));
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

    @Override
    public Timeline getProjectTimeline(ProjectId projectId) {
        return repository.findById(projectId)
                         .map(CrowdfundingProject::getId)
                         .map(id -> new Timeline(id, repository.findTimelineEvents(id)))
                         .orElseThrow(() -> new CrowdfundingProjectException(CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND));

    }

    @Override
    public void createProjectTimeline(ProjectId projectId, List<TimelineEventCommand> events) {

        Timeline existingTimeline = getProjectTimeline(projectId);
        transactionalManager.executeInTransaction(() -> projectTimelineService.updateTimeline(
                existingTimeline, events.stream()
                                        .map(projectTimelineService::createNewEvent)
                                        .toList()));

    }


    @Override
    public void addProjectTimelineEvent(ProjectId projectId, TimelineEventCommand event) {
        Timeline existingTimeline = getProjectTimeline(projectId);
        transactionalManager.executeInTransaction(() -> projectTimelineService.updateTimeline(
                existingTimeline,
                List.of(projectTimelineService.createNewEvent(event))));
    }

    @Override
    public void updateProjectTimelineEvent(ProjectId projectId, TimelineEventId timelineEventId, TimelineEventCommand event) {
        Timeline existingTimeline = getProjectTimeline(projectId);
        List<TimelineEvent> events = projectTimelineService.replace(timelineEventId, event, existingTimeline);
        transactionalManager.executeInTransaction(() -> projectTimelineService.updateTimeline(existingTimeline, events));

    }



    @Override
    public void deleteProjectTimelineEvent(ProjectId projectId, TimelineEventId timelineEventId) {
        Timeline existingTimeline = getProjectTimeline(projectId);
        List<TimelineEvent> events = projectTimelineService.remove(timelineEventId, existingTimeline);
        transactionalManager.executeInTransaction(() -> projectTimelineService.updateTimeline(existingTimeline, events));
    }




}
