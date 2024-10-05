package nextcrowd.crowdfunding.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nextcrowd.crowdfunding.project.command.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.project.event.CrowdfundingProjectSubmittedEvent;
import nextcrowd.crowdfunding.project.exception.ValidationException;
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

    @Test
    void shouldNotCreateProjectValidationFails() {
        // given
        Instant now = Instant.now();
        SubmitCrowdfundingProjectCommand projectCreationCommand = buildSubmitCommand(now, ProjectOwner.builder()
                                                                                                      .id("1")
                                                                                                      .imageUrl("ownerImageUrl")
                                                                                                      .name("owner1")
                                                                                                      .build());
        when(validationService.validateProjectSubmission(projectCreationCommand)).thenReturn(List.of(new ProjectValidationService.ValidationFailure("reason")));

        // when
        // then
        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> projectService.submitProject(projectCreationCommand));

    }

    @Test
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