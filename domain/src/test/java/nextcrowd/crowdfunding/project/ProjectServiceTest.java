package nextcrowd.crowdfunding.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import nextcrowd.crowdfunding.project.command.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectApprovedEvent;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.exception.ProjectApprovalException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.ProjectReward;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;
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
        projectService = new ProjectService(validationService, crowdfundingProjectRepository, eventPublisher);
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
            // id generation service?

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
            assertThatExceptionOfType(ProjectApprovalException.class)
                    .isThrownBy(() -> projectService.approve(projectId, command))
                    .matches(e -> e.getReason() == ProjectApprovalException.Reason.PROJECT_NOT_FOUND);

        }

        @Test
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
            assertThatExceptionOfType(ProjectApprovalException.class)
                    .isThrownBy(() -> projectService.approve(projectId, command))
                    .matches(e -> e.getReason() == ProjectApprovalException.Reason.INVALID_PROJECT_STATUS);
        }

        @Test
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
            assertThatExceptionOfType(ProjectApprovalException.class)
                    .isThrownBy(() -> projectService.approve(projectId, command))
                    .matches(e -> e.getReason() == ProjectApprovalException.Reason.INVALID_COMMAND);
        }

        @Test
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
        void shouldDoNothingIfApproved() {
            // given
            ProjectId projectId = randomProjectId();
            when(crowdfundingProjectRepository.findById(projectId))
                    .thenReturn(Optional.of(buildProjectApproved(projectId)));
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

    private static CrowdfundingProject buildProjectApproved(ProjectId projectId) {
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
        return new ProjectId(UUID.randomUUID().toString());
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