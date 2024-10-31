package nextcrowd.crowdfunding.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.github.f4b6a3.uuid.UuidCreator;

import nextcrowd.crowdfunding.project.command.AddInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.CancelInvestmentCommand;
import nextcrowd.crowdfunding.project.command.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentAddedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectIssuedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentCanceledEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectPendingInvestmentConfirmedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.BakerId;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.MoneyTransferId;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;
import nextcrowd.crowdfunding.project.port.TransactionalManager;
import nextcrowd.crowdfunding.project.service.ProjectValidationService;

class ProjectServiceTest {

    private ProjectValidationService validationService;
    private CrowdfundingProjectRepository crowdfundingProjectRepository;
    private EventPublisher eventPublisher;
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        validationService = Mockito.mock(ProjectValidationService.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        crowdfundingProjectRepository = Mockito.mock(CrowdfundingProjectRepository.class);
        TransactionalManager fakeTransactionalManager = new TransactionalManager() {
            @Override
            public void executeInTransaction(Runnable runnable) {
                runnable.run();
            }

            @Override
            public <T> T executeInTransaction(Supplier<T> supplier) {
                return supplier.get();
            }
        };
        projectService = new ProjectService(validationService, crowdfundingProjectRepository, eventPublisher, fakeTransactionalManager);
    }

    @Nested
    @DisplayName("project submission")
    class ProjectSubmissionTest {

        @Test
        @DisplayName("should not create a project if any validation fails")
        void shouldNotCreateProjectValidationFails() {
            // given
            Instant now = Instant.now();
            SubmitCrowdfundingProjectCommand projectCreationCommand = buildSubmitCommand(now, ProjectOwner.builder()
                                                                                                          .id("1")
                                                                                                          .imageUrl("ownerImageUrl")
                                                                                                          .name("owner1")
                                                                                                          .build());
            when(validationService.validateProjectSubmission(projectCreationCommand)).thenReturn(buildValidationFailureList());

            // when
            // then
            assertThatExceptionOfType(ValidationException.class)
                    .isThrownBy(() -> projectService.submitProject(projectCreationCommand));

        }

        @Test
        @DisplayName("should create a project and send an event")
        void shouldStoreProjectAndPublishEvent() {
            // given
            Instant now = Instant.now();
            ProjectOwner projectOwner = ProjectOwner.builder()
                                                    .id("1")
                                                    .imageUrl("ownerImageUrl")
                                                    .name("owner1")
                                                    .build();
            SubmitCrowdfundingProjectCommand projectCreationCommand = buildSubmitCommand(now, projectOwner);
            when(validationService.validateProjectSubmission(projectCreationCommand)).thenReturn(Collections.emptyList());
            when(crowdfundingProjectRepository.save(argThat(p -> p.getId() != null))).thenAnswer(returnsFirstArg());

            // when
            ProjectId projectId = projectService.submitProject(projectCreationCommand);
            //then
            verify(eventPublisher).publish(CrowdfundingProjectSubmittedEvent.builder()
                                                                            .projectId(projectId)
                                                                            .projectOwner(projectOwner)
                                                                            .build());
        }

    }

    private static List<ProjectValidationService.ValidationFailure> buildValidationFailureList() {
        return List.of(new ProjectValidationService.ValidationFailure(
                "reason"));
    }

    @Nested
    @DisplayName("project approval")
    class ProjectApprovalTest {

        @Test
        @DisplayName("should fail if project not found")
        void shouldFailIfProjectNotFound() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId)).thenReturn(Optional.empty());
            ApproveCrowdfundingProjectCommand command = ApproveCrowdfundingProjectCommand.builder()
                                                                                         .risk(3)
                                                                                         .expectedProfit(new BigDecimal("10.00"))
                                                                                         .minimumInvestment(new BigDecimal("300.00"))
                                                                                         .build();
            // when
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.approve(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
        @DisplayName("should fail if project not submitted")
        void shouldFailIfProjectNotSubmitted() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectIssued(projectId)));
            ApproveCrowdfundingProjectCommand command = ApproveCrowdfundingProjectCommand.builder()
                                                                                         .risk(3)
                                                                                         .expectedProfit(new BigDecimal("10.00"))
                                                                                         .minimumInvestment(new BigDecimal("300.00"))
                                                                                         .build();
            // when
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.approve(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
        @DisplayName("should fail if command is not valid")
        void shouldFailInvalidCommand() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectSubmitted(projectId)));
            ApproveCrowdfundingProjectCommand command = ApproveCrowdfundingProjectCommand.builder()
                                                                                         .risk(3)
                                                                                         .expectedProfit(new BigDecimal("10.00"))
                                                                                         .minimumInvestment(new BigDecimal("300.00"))
                                                                                         .build();
            when(validationService.validateProjectApproval(command)).thenReturn(buildValidationFailureList());
            // when
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.approve(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_COMMAND);
        }

        @Test
        @DisplayName("should approve project, fill missing info and publish event")
        void shouldApproveWithMissingInfoAndPublishEvent() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectSubmitted(projectId)));
            ApproveCrowdfundingProjectCommand command = ApproveCrowdfundingProjectCommand.builder()
                                                                                         .risk(3)
                                                                                         .expectedProfit(new BigDecimal("10.00"))
                                                                                         .minimumInvestment(new BigDecimal("300.00"))
                                                                                         .build();
            when(validationService.validateProjectApproval(command)).thenReturn(Collections.emptyList());

            // when
            projectService.approve(projectId, command);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getStatus() == CrowdfundingProject.Status.APPROVED)
                    .matches(p -> p.getRisk() == command.getRisk())
                    .matches(p -> p.getMinimumInvestment().equals(command.getMinimumInvestment()))
                    .matches(p -> p.getExpectedProfit().equals(command.getExpectedProfit()));
            verify(eventPublisher).publish(CrowdfundingProjectApprovedEvent.builder()
                                                                           .expectedProfit(command.getExpectedProfit())
                                                                           .risk(command.getRisk())
                                                                           .minimumInvestment(command.getMinimumInvestment())
                                                                           .projectId(projectId)
                                                                           .build());


        }

        @Test
        @DisplayName("should do nothing if already approved")
        void shouldDoNothingIfApproved() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectApproved(projectId, new BigDecimal(0), Collections.emptyList(), List.of(), List.of())));
            ApproveCrowdfundingProjectCommand command = ApproveCrowdfundingProjectCommand.builder()
                                                                                         .risk(3)
                                                                                         .expectedProfit(new BigDecimal("10.00"))
                                                                                         .minimumInvestment(new BigDecimal("300.00"))
                                                                                         .build();

            // when
            projectService.approve(projectId, command);

            verify(crowdfundingProjectRepository, times(0)).save(any());
            verifyNoInteractions(eventPublisher);
        }

    }

    @Nested
    @DisplayName("project rejection")
    class ProjectRejectionTest {

        @Test
        @DisplayName("should fail if project not found")
        void shouldFailIfProjectNotFound() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId)).thenReturn(Optional.empty());
            // when
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.reject(projectId))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
        @DisplayName("should fail if project not submitted")
        void shouldFailIfProjectNotSubmitted() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectIssued(projectId)));

            // when
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.reject(projectId))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
        @DisplayName("should reject and publish event")
        void shouldRejectAndPublishEvent() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectSubmitted(projectId)));

            // when
            projectService.reject(projectId);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getStatus() == CrowdfundingProject.Status.REJECTED);
            verify(eventPublisher).publish(CrowdfundingProjectRejectedEvent.builder()
                                                                           .projectId(projectId)
                                                                           .build());


        }

        @Test
        @DisplayName("should do nothing if already rejected")
        void shouldDoNothingIfRejected() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectRejected(projectId)));

            // when
            projectService.reject(projectId);

            // then
            verify(crowdfundingProjectRepository, times(0)).save(any());
            verifyNoInteractions(eventPublisher);
        }

    }

    @Nested
    @DisplayName("project contribution")
    class ProjectContributionTest {

        @Test
        @DisplayName("should fail if project not found")
        void shouldFailIfProjectNotFound() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId)).thenReturn(Optional.empty());
            // when
            AddInvestmentCommand command = AddInvestmentCommand.builder()
                                                               .amount(new BigDecimal(300))
                                                               .bakerId(new BakerId("contributorId"))
                                                               .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.addInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
        @DisplayName("should fail if project not approved")
        void shouldFailIfProjectNotApproved() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectIssued(projectId)));

            // when
            AddInvestmentCommand command = AddInvestmentCommand.builder()
                                                               .amount(new BigDecimal(300))
                                                               .bakerId(new BakerId("contributorId"))
                                                               .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.addInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
        @DisplayName("should invest in project")
        void shouldInvestInProject() {
            // given
            ProjectId projectId = randomProjectId();
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), Collections.emptyList(), List.of(), List.of());
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));
            AddInvestmentCommand command = AddInvestmentCommand.builder()
                                                               .amount(new BigDecimal(300))
                                                               .bakerId(new BakerId("bakerId"))
                                                               .build();

            // when
            projectService.addInvestment(projectId, command);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getPendingInvestments() != null && p.getPendingInvestments()
                                                                        .stream()
                                                                        .anyMatch(i -> i.getAmount().equals(command.getAmount()) && i.getBakerId()
                                                                                                                                     .equals(command.getBakerId())));
            verify(eventPublisher).publish(CrowdfundingProjectPendingInvestmentAddedEvent.builder()
                                                                                         .projectId(projectId)
                                                                                         .amount(command.getAmount())
                                                                                         .bakerId(command.getBakerId())
                                                                                         .build());


        }

        @Test
        @DisplayName("should invest in project twice")
        void shouldInvestInProjectTwice() {
            // given
            ProjectId projectId = randomProjectId();
            AddInvestmentCommand command = AddInvestmentCommand.builder()
                                                               .amount(new BigDecimal(300))
                                                               .bakerId(new BakerId("bakerId"))
                                                               .build();
            Investment previousInvestment = buildPendingInvestment(command.getBakerId(), new BigDecimal(1000));
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), List.of(previousInvestment), List.of(), List.of());
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));

            // when
            projectService.addInvestment(projectId, command);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getPendingInvestments() != null && p.getPendingInvestments()
                                                                        .stream()
                                                                        .anyMatch(i -> i.getAmount()
                                                                                        .equals(command.getAmount().add(previousInvestment.getAmount()))
                                                                                       && i.getBakerId()
                                                                                           .equals(command.getBakerId())));
            verify(eventPublisher).publish(CrowdfundingProjectPendingInvestmentAddedEvent.builder()
                                                                                         .projectId(projectId)
                                                                                         .amount(command.getAmount())
                                                                                         .bakerId(command.getBakerId())
                                                                                         .build());


        }


    }

    @Nested
    @DisplayName("investment confirmation")
    class InvestmentConfirmation {

        @Test
        @DisplayName("should fail if project not found")
        void shouldFailIfProjectNotFound() {
            // given
            ProjectId projectId = randomProjectId();
            BakerId bakerId = randomBakerId();
            when(crowdfundingProjectRepository.findById(projectId)).thenReturn(Optional.empty());
            // when
            ConfirmInvestmentCommand command = ConfirmInvestmentCommand.builder()
                                                                       .bakerId(bakerId)
                                                                       .moneyTransferId(randomMoneyTransferId())
                                                                       .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.confirmInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
        @DisplayName("should fail if project not approved")
        void shouldFailIfProjectNotApproved() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectIssued(projectId)));

            // when
            ConfirmInvestmentCommand command = ConfirmInvestmentCommand.builder()
                                                                       .bakerId(randomBakerId())
                                                                       .moneyTransferId(randomMoneyTransferId())
                                                                       .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.confirmInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
        public void shouldFailIfPendingInvestmentMissing() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectApproved(projectId, new BigDecimal(3000), Collections.emptyList(), List.of(), List.of())));

            // when
            ConfirmInvestmentCommand command = ConfirmInvestmentCommand.builder()
                                                                       .bakerId(randomBakerId())
                                                                       .moneyTransferId(randomMoneyTransferId())
                                                                       .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.confirmInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVESTMENT_NOT_FOUND);
        }

        @Test
        void shouldDoNothingIfAlreadyConfirmed() {
            // given
            BakerId bakerId = randomBakerId();
            ProjectId projectId = randomProjectId();
            Investment existingInvestment = buildAcceptedInvestment(bakerId, new BigDecimal(300));
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), List.of(), List.of(existingInvestment), List.of());
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));
            ConfirmInvestmentCommand command = ConfirmInvestmentCommand.builder()
                                                                       .bakerId(bakerId)
                                                                       .moneyTransferId(randomMoneyTransferId())
                                                                       .build();

            // when
            projectService.confirmInvestment(projectId, command);

            // then
            verify(crowdfundingProjectRepository, times(0)).save(any());
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("should confirm accept the investment after receiving the money")
        void shouldAcceptInvestment() {
            // given
            BakerId bakerId = new BakerId("bakerId");
            ProjectId projectId = randomProjectId();
            Investment existingInvestment = buildPendingInvestment(bakerId, new BigDecimal(300));
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), List.of(existingInvestment), List.of(), List.of());
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));
            ConfirmInvestmentCommand command = ConfirmInvestmentCommand.builder()
                                                                       .bakerId(bakerId)
                                                                       .moneyTransferId(randomMoneyTransferId())
                                                                       .build();

            // when
            projectService.confirmInvestment(projectId, command);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getPendingInvestments() != null && p.getPendingInvestments()
                                                                        .stream()
                                                                        .noneMatch(i -> i.equals(existingInvestment)))
                    .matches(p -> p.getCollectedAmount().equals(approvedProject.getCollectedAmount().add(existingInvestment.getAmount())));
            verify(eventPublisher).publish(CrowdfundingProjectPendingInvestmentConfirmedEvent.builder()
                                                                                             .projectId(projectId)
                                                                                             .amount(existingInvestment.getAmount())
                                                                                             .bakerId(command.getBakerId())
                                                                                             .moneyTransferId(command.getMoneyTransferId())
                                                                                             .build());
        }

    }

    private static Investment buildPendingInvestment(BakerId bakerId, BigDecimal amount) {
        return Investment.builder()
                        .id(new InvestmentId(UuidCreator.getTimeOrderedEpoch().toString()))
                         .bakerId(bakerId)
                         .amount(amount)
                         .status(InvestmentStatus.PENDING)
                         .build();
    }

    private static Investment buildAcceptedInvestment(BakerId bakerId, BigDecimal amount) {
        return Investment.builder()
                         .id(new InvestmentId(UuidCreator.getTimeOrderedEpoch().toString()))
                         .bakerId(bakerId)
                         .amount(amount)
                         .moneyTransferId(randomMoneyTransferId())
                         .status(InvestmentStatus.ACCEPTED)
                         .build();
    }

    @Nested
    @DisplayName("investment cancellation")
    class InvestmentCancellation {

        @Test
        @DisplayName("should fail if project not found")
        void shouldFailIfProjectNotFound() {
            // given
            ProjectId projectId = randomProjectId();
            BakerId bakerId = randomBakerId();
            when(crowdfundingProjectRepository.findById(projectId)).thenReturn(Optional.empty());
            // when
            CancelInvestmentCommand command = CancelInvestmentCommand.builder()
                                                                     .bakerId(bakerId)
                                                                     .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.cancelInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
        @DisplayName("should fail if project not approved")
        void shouldFailIfProjectNotApproved() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectIssued(projectId)));

            // when
            CancelInvestmentCommand command = CancelInvestmentCommand.builder()
                                                                     .bakerId(randomBakerId())
                                                                     .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.cancelInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
        @DisplayName("should fail if investment is missing")
        public void shouldFailIfPendingInvestmentMissing() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectApproved(projectId, new BigDecimal(3000), Collections.emptyList(), List.of(), List.of())));

            // when
            CancelInvestmentCommand command = CancelInvestmentCommand.builder()
                                                                     .bakerId(randomBakerId())
                                                                     .build();
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.cancelInvestment(projectId, command))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVESTMENT_NOT_FOUND);
        }

        @Test
        @DisplayName("do nothing if canceled")
        void shouldDoNothingIfAlreadyCanceled() {
            // given
            BakerId bakerId = randomBakerId();
            ProjectId projectId = randomProjectId();
            Investment existingInvestment = buildRefusedInvestment(bakerId);
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), List.of(), List.of(), List.of(existingInvestment));
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));
            CancelInvestmentCommand command = CancelInvestmentCommand.builder()
                                                                     .bakerId(bakerId)
                                                                     .build();

            // when
            projectService.cancelInvestment(projectId, command);

            // then
            verify(crowdfundingProjectRepository, times(0)).save(any());
            verifyNoInteractions(eventPublisher);
        }

        @Test
        @DisplayName("should cancel the investment")
        void shouldCancelInvestment() {
            // given
            BakerId bakerId = new BakerId("bakerId");
            ProjectId projectId = randomProjectId();
            Investment existingInvestment = buildPendingInvestment(bakerId, new BigDecimal(300));
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), List.of(existingInvestment), List.of(), List.of());
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));
            CancelInvestmentCommand command = CancelInvestmentCommand.builder()
                                                                     .bakerId(bakerId)
                                                                     .build();

            // when
            projectService.cancelInvestment(projectId, command);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getPendingInvestments() != null && p.getPendingInvestments()
                                                                        .stream()
                                                                        .noneMatch(i -> i.equals(existingInvestment)))
                    .matches(p -> p.getCollectedAmount().equals(approvedProject.getCollectedAmount()));
            verify(eventPublisher).publish(CrowdfundingProjectPendingInvestmentCanceledEvent.builder()
                                                                                            .projectId(projectId)
                                                                                            .amount(existingInvestment.getAmount())
                                                                                            .bakerId(command.getBakerId())
                                                                                            .build());
        }

    }

    private static Investment buildRefusedInvestment(BakerId bakerId) {
        return Investment.builder()
                         .id(new InvestmentId(UuidCreator.getTimeOrderedEpoch().toString()))
                         .bakerId(bakerId)
                         .amount(new BigDecimal(300))
                         .status(InvestmentStatus.REFUSED)
                         .build();
    }

    private static MoneyTransferId randomMoneyTransferId() {
        return new MoneyTransferId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    private static BakerId randomBakerId() {
        return new BakerId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    @Nested
    @DisplayName("project issuing")
    class ProjectIssuingTest {

        @Test
        @DisplayName("should fail if project not found")
        void shouldFailIfProjectNotFound() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId)).thenReturn(Optional.empty());
            // when
            // then
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.issue(projectId))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
        @DisplayName("should fail if project not approved")
        void shouldFailIfProjectNotApproved() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectSubmitted(projectId)));

            // when
            // then
            assertThatExceptionOfType(CrowdfundingProjectException.class)
                    .isThrownBy(() -> projectService.issue(projectId))
                    .matches(e -> e.getReason() == CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
        @DisplayName("should issue and publish event")
        void shouldRejectAndPublishEvent() {
            // given
            ProjectId projectId = randomProjectId();
            CrowdfundingProject approvedProject = buildProjectApproved(projectId, new BigDecimal(0), Collections.emptyList(), List.of(), List.of());
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(approvedProject));

            // when
            projectService.issue(projectId);

            // then
            ArgumentCaptor<CrowdfundingProject> captor = ArgumentCaptor.forClass(CrowdfundingProject.class);
            verify(crowdfundingProjectRepository).save(captor.capture());
            assertThat(captor.getValue())
                    .matches(p -> p.getStatus() == CrowdfundingProject.Status.ISSUED);
            verify(eventPublisher).publish(CrowdfundingProjectIssuedEvent.builder()
                                                                         .projectId(projectId)
                                                                         .build());
        }

    }

    private static CrowdfundingProject buildProjectSubmitted(ProjectId projectId) {
        return CrowdfundingProject.builder()
                                  .id(projectId)
                                  .status(CrowdfundingProject.Status.SUBMITTED)
                                  .projectStartDate(Instant.now().plus(1, ChronoUnit.DAYS))
                                  .projectEndDate(Instant.now().plus(60, ChronoUnit.DAYS))
                                  .projectVideoUrl("videoUrl")
                                  .owner(ProjectOwner.builder()
                                                     .name("ownerName")
                                                     .imageUrl("ownerImageUrl")
                                                     .id("ownerId")
                                                     .build())
                                  .title("projectTitle")
                                  .currency("EUR")
                                  .requestedAmount(new BigDecimal(300_000))
                                  .description("aShortDescription")
                                  .longDescription("aLongDescription")
                                  .imageUrl("projectImageUrl")
                                  .rewards(List.of(
                                          ProjectReward.builder()
                                                       .description("aRewardDescription1")
                                                       .imageUrl("rewardImageUrl1")
                                                       .name("rewardName1")
                                                       .build(),
                                          ProjectReward.builder()
                                                       .description("aRewardDescription2")
                                                       .imageUrl("rewardImageUrl2")
                                                       .name("rewardName2")
                                                       .build()))
                                  .build();
    }

    private static CrowdfundingProject buildProjectRejected(ProjectId projectId) {
        return CrowdfundingProject.builder()
                                  .id(projectId)
                                  .status(CrowdfundingProject.Status.REJECTED)
                                  .projectStartDate(Instant.now().plus(1, ChronoUnit.DAYS))
                                  .projectEndDate(Instant.now().plus(60, ChronoUnit.DAYS))
                                  .projectVideoUrl("videoUrl")
                                  .owner(ProjectOwner.builder()
                                                     .name("ownerName")
                                                     .imageUrl("ownerImageUrl")
                                                     .id("ownerId")
                                                     .build())
                                  .title("projectTitle")
                                  .currency("EUR")
                                  .requestedAmount(new BigDecimal(300_000))
                                  .description("aShortDescription")
                                  .longDescription("aLongDescription")
                                  .imageUrl("projectImageUrl")
                                  .rewards(List.of(
                                          ProjectReward.builder()
                                                       .description("aRewardDescription1")
                                                       .imageUrl("rewardImageUrl1")
                                                       .name("rewardName1")
                                                       .build(),
                                          ProjectReward.builder()
                                                       .description("aRewardDescription2")
                                                       .imageUrl("rewardImageUrl2")
                                                       .name("rewardName2")
                                                       .build()))
                                  .build();
    }

    private static CrowdfundingProject buildProjectApproved(
            ProjectId projectId, BigDecimal collectedAmount, List<Investment> pendingInvestments,
            List<Investment> acceptedInvestments, List<Investment> refusedInvestments) {

        List<Investment> investments = new ArrayList<>();
        investments.addAll(pendingInvestments);
        investments.addAll(acceptedInvestments);
        investments.addAll(refusedInvestments);
        return CrowdfundingProject.builder()
                                  .id(projectId)
                                  .status(CrowdfundingProject.Status.APPROVED)
                                  .risk(4)
                                  .expectedProfit(new BigDecimal(10))
                                  .minimumInvestment(new BigDecimal(3000))
                                  .projectStartDate(Instant.now().plus(1, ChronoUnit.DAYS))
                                  .projectEndDate(Instant.now().plus(60, ChronoUnit.DAYS))
                                  .projectVideoUrl("videoUrl")
                                  .owner(ProjectOwner.builder()
                                                     .name("ownerName")
                                                     .imageUrl("ownerImageUrl")
                                                     .id("ownerId")
                                                     .build())
                                  .title("projectTitle")
                                  .currency("EUR")
                                  .requestedAmount(new BigDecimal(300_000))
                                  .collectedAmount(collectedAmount)
                                  .description("aShortDescription")
                                  .longDescription("aLongDescription")
                                  .imageUrl("projectImageUrl")
                                  .rewards(List.of(
                                          ProjectReward.builder()
                                                       .description("aRewardDescription1")
                                                       .imageUrl("rewardImageUrl1")
                                                       .name("rewardName1")
                                                       .build(),
                                          ProjectReward.builder()
                                                       .description("aRewardDescription2")
                                                       .imageUrl("rewardImageUrl2")
                                                       .name("rewardName2")
                                                       .build()))
                                  .investments(investments)
                                  .build();
    }

    private static CrowdfundingProject buildProjectIssued(ProjectId projectId) {
        return CrowdfundingProject.builder()
                                  .id(projectId)
                                  .status(CrowdfundingProject.Status.ISSUED)
                                  .risk(4)
                                  .expectedProfit(new BigDecimal(10))
                                  .minimumInvestment(new BigDecimal(3000))
                                  .projectStartDate(Instant.now().plus(1, ChronoUnit.DAYS))
                                  .projectEndDate(Instant.now().plus(60, ChronoUnit.DAYS))
                                  .projectVideoUrl("videoUrl")
                                  .owner(ProjectOwner.builder()
                                                     .name("ownerName")
                                                     .imageUrl("ownerImageUrl")
                                                     .id("ownerId")
                                                     .build())
                                  .title("projectTitle")
                                  .currency("EUR")
                                  .requestedAmount(new BigDecimal(300_000))
                                  .description("aShortDescription")
                                  .longDescription("aLongDescription")
                                  .imageUrl("projectImageUrl")
                                  .rewards(List.of(
                                          ProjectReward.builder()
                                                       .description("aRewardDescription1")
                                                       .imageUrl("rewardImageUrl1")
                                                       .name("rewardName1")
                                                       .build(),
                                          ProjectReward.builder()
                                                       .description("aRewardDescription2")
                                                       .imageUrl("rewardImageUrl2")
                                                       .name("rewardName2")
                                                       .build()))
                                  .build();
    }

    private static ProjectId randomProjectId() {
        return new ProjectId(UuidCreator.getTimeOrderedEpoch().toString());
    }

    private static SubmitCrowdfundingProjectCommand buildSubmitCommand(Instant now, ProjectOwner projectOwner) {
        return SubmitCrowdfundingProjectCommand
                .builder()
                .projectStartDate(now.plus(1, ChronoUnit.DAYS))
                .projectEndDate(now.plus(60, ChronoUnit.DAYS))
                .projectVideoUrl("videoUrl")
                .owner(projectOwner)
                .title("projectTitle")
                .currency("EUR")
                .requestedAmount(300_000d)
                .description("aShortDescription")
                .longDescription("aLongDescription")
                .imageUrl("projectImageUrl")
                .rewards(List.of(
                        ProjectReward.builder()
                                     .description("aRewardDescription1")
                                     .imageUrl("rewardImageUrl1")
                                     .name("rewardName1")
                                     .build(),
                        ProjectReward.builder()
                                     .description("aRewardDescription2")
                                     .imageUrl("rewardImageUrl2")
                                     .name("rewardName2")
                                     .build()))
                .build();
    }


}