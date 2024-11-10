package nextcrowd.crowdfunding.infrastructure.api;

import static nextcrowd.crowdfunding.infrastructure.TestUtils.buildRandomInstant;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import nextcrowd.crowdfunding.admin.api.model.AddInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.admin.api.model.CancelInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ConfirmInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.infrastructure.SecurityConfiguration;
import nextcrowd.crowdfunding.infrastructure.TestUtils;
import nextcrowd.crowdfunding.infrastructure.security.persistence.User;
import nextcrowd.crowdfunding.infrastructure.security.persistence.UserRepository;
import nextcrowd.crowdfunding.infrastructure.security.service.JwtService;
import nextcrowd.crowdfunding.project.ProjectService;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;

@WebMvcTest
@Import({JwtService.class, SecurityConfiguration.class})
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "security.jwt.expiration-time: 3600000",
        "security.jwt.secret-key: "
        +
        "Mzg4NTUwMGEzN2ExZmFjYTMzNmY5MzNjZTYxNzY5NTIwNjBhYTg1OTM5ODA4YzEwMWJiZjk1MTA0OTIxMzVmYjhkMzZiZjFhNmY1NjgyYmQ3MTZiNDNkY2M4YWIyNDRhNTUwNzJiYTEzNzY0NDE4YWJhMzg1YTNkYTJjMDJiZGQ=",
})
class PublicAdminProjectControllerTest {

    private static final User ADMIN_USER = TestUtils.buildRandomUser(Set.of("ROLE_ADMIN"));
    private static final User APPLICATION_PROJECT_USER = TestUtils.buildRandomUser(Set.of("ROLE_PROJECT"));
    @MockBean
    private ProjectService projectService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        when(userRepository.findByEmail(eq(ADMIN_USER.getEmail()))).thenReturn(Optional.of(ADMIN_USER));
        when(userRepository.findByEmail(eq(APPLICATION_PROJECT_USER.getEmail()))).thenReturn(Optional.of(APPLICATION_PROJECT_USER));

    }

    @Nested
    @DisplayName("Read Project API")
    class TestReadProjectAPI {

        @Test
        @DisplayName("By id - non admin user")
        void testGetProjectByIdNonAdminUser() throws Exception {
            // Arrange
            CrowdfundingProject project = TestUtils.buildRandomProject(TestUtils.buildRandomProjectOwner())
                                                   .toBuilder()
                                                   .collectedAmount(new BigDecimal("300.00"))
                                                   .projectStartDate(buildRandomInstant().truncatedTo(ChronoUnit.SECONDS))
                                                   .projectEndDate(buildRandomInstant().truncatedTo(ChronoUnit.SECONDS))
                                                   .build();
            ProjectId projectId = project.getId();
            when(projectService.getById(projectId)).thenReturn(Optional.of(project));

            // Act & Assert

            mockMvc.perform(get("/admin/projects/{id}", projectId.id())
                                    .header("Authorization", "Bearer " + jwtService.generateToken(APPLICATION_PROJECT_USER)))
                   .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("By id")
        void testGetProjectById() throws Exception {
            // Arrange
            CrowdfundingProject project = TestUtils.buildRandomProject(TestUtils.buildRandomProjectOwner())
                                                   .toBuilder()
                                                   .collectedAmount(new BigDecimal("300.00"))
                                                   .projectStartDate(buildRandomInstant().truncatedTo(ChronoUnit.SECONDS))
                                                   .projectEndDate(buildRandomInstant().truncatedTo(ChronoUnit.SECONDS))
                                                   .build();
            ProjectId projectId = project.getId();
            when(projectService.getById(projectId)).thenReturn(Optional.of(project));

            // Act & Assert

            mockMvc.perform(get("/admin/projects/{id}", projectId.id())
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").value(projectId.id()))
                   .andExpect(jsonPath("$.title").value(project.getTitle()))
                   .andExpect(jsonPath("$.status").value(project.getStatus().toString()))
                   .andExpect(jsonPath("$.imageUrl").value(project.getImageUrl()))
                   .andExpect(jsonPath("$.currency").value(project.getCurrency()))
                   .andExpect(jsonPath("$.longDescription").value(project.getLongDescription()))
                   .andExpect(jsonPath("$.projectVideoUrl").value(project.getProjectVideoUrl()))
                   .andExpect(jsonPath("$.description").value(project.getDescription()))
                   .andExpect(jsonPath("$.requestedAmount").value(project.getRequestedAmount().doubleValue()))
                   .andExpect(jsonPath("$.collectedAmount").value(project.getCollectedAmount().orElseThrow().doubleValue()))
                   .andExpect(jsonPath("$.projectStartDate").value(project.getProjectStartDate().toString()))
                   .andExpect(jsonPath("$.projectEndDate").value(project.getProjectEndDate().toString()))
                   .andExpect(jsonPath("$.owner.id").value(project.getOwner().getId()))
                   .andExpect(jsonPath("$.owner.name").value(project.getOwner().getName()))
                   .andExpect(jsonPath("$.owner.imageUrl").value(project.getOwner().getImageUrl()));
        }

        @Test
        @DisplayName("By id - Not Found")
        void testGetProjectByIdNotFound() throws Exception {
            String projectId = "non-existent-project-id";

            when(projectService.getById(new ProjectId(projectId))).thenReturn(Optional.empty());

            mockMvc.perform(get("/admin/projects/{id}", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER)))
                   .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Get pending review projects")
        void testGetPendingReviewProjects() throws Exception {
            String cursor = "test-cursor";
            int limit = 10;
            List<CrowdfundingProject> projects = IntStream.range(0, 15)
                                                          .mapToObj(_ -> TestUtils.buildRandomProject(TestUtils.buildRandomProjectOwner()))
                                                          .toList();
            when(projectService.getPendingReviewProjects(any())).thenReturn(projects.stream());

            mockMvc.perform(get("/admin/projects/pending-review")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .param("cursor", cursor)
                                    .param("limit", String.valueOf(limit)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data.length()").value(10))
                   .andExpect(jsonPath("$.hasMore").value(true))
                   .andExpect(jsonPath(
                           "$.data[*].id",
                           containsInAnyOrder(projects.stream().map(CrowdfundingProject::getId).map(ProjectId::id).limit(10).toArray(String[]::new))))
            ;
        }

        @Test
        @DisplayName("Get published projects")
        void testGetPublishedProjects() throws Exception {
            String cursor = "test-cursor";
            int limit = 10;
            List<CrowdfundingProject> projects = IntStream.range(0, 15)
                                                          .mapToObj(_ -> TestUtils.buildRandomProject(TestUtils.buildRandomProjectOwner()))
                                                          .toList();
            when(projectService.getPublishedProjects(any())).thenReturn(projects.stream());

            mockMvc.perform(get("/admin/projects/published")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .param("cursor", cursor)
                                    .param("limit", String.valueOf(limit)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.data").isArray())
                   .andExpect(jsonPath("$.data.length()").value(10))
                   .andExpect(jsonPath("$.hasMore").value(true))
                   .andExpect(jsonPath(
                           "$.data[*].id",
                           containsInAnyOrder(projects.stream().map(CrowdfundingProject::getId).map(ProjectId::id).limit(10).toArray(String[]::new))));
        }

    }

    @Nested
    @DisplayName("Project API")
    class TestProjectAPI {

        @Test
        @DisplayName("Submit a new project")
        void testSubmitProject() throws Exception {
            SubmitCrowdfundingProjectCommand submitCommandApi = buildRandomSubmitCrowdfundingProjectCommand();
            ProjectId expectedProjectId = ProjectId.generateId();
            when(projectService.submitProject(any())).thenReturn(expectedProjectId);

            // Act & Assert
            mockMvc.perform(post("/admin/projects")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(submitCommandApi)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").value(expectedProjectId.id()));
        }

        @Test
        @DisplayName("Submit a new project with validation error")
        void testSubmitProjectWithValidationError() throws Exception {
            SubmitCrowdfundingProjectCommand submitCommandApi = buildRandomSubmitCrowdfundingProjectCommand();
            when(projectService.submitProject(any())).thenThrow(new ValidationException("Validation error"));

            mockMvc.perform(post("/admin/projects")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(submitCommandApi)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Validation error"));
        }

        @Test
        @DisplayName("Approve a project")
        void testApproveProject() throws Exception {
            String projectId = "test-project-id";
            ApproveCrowdfundingProjectCommand approveCommand = new ApproveCrowdfundingProjectCommand()
                    .expectedProfit(300.00)
                    .risk(5)
                    .minimumInvestment(200.00);

            doNothing().when(projectService).approve(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/approve", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(approveCommand)))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Approve a project with validation error")
        void testApproveProjectWithValidationError() throws Exception {
            String projectId = "test-project-id";
            ApproveCrowdfundingProjectCommand approveCommand = new ApproveCrowdfundingProjectCommand()
                    .expectedProfit(300.00)
                    .risk(5)
                    .minimumInvestment(200.00);

            doThrow(new ValidationException("Validation error")).when(projectService).approve(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/approve", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(approveCommand)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Validation error"));
        }

        @Test
        @DisplayName("Approve a project with status failure")
        void testApproveProjectWithStatusFailure() throws Exception {
            String projectId = "test-project-id";
            ApproveCrowdfundingProjectCommand approveCommand = new ApproveCrowdfundingProjectCommand()
                    .expectedProfit(300.00)
                    .risk(5)
                    .minimumInvestment(200.00);

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectService)
                                                                                                                 .approve(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/approve", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(approveCommand)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

        @Test
        @DisplayName("Reject a project")
        void testRejectProject() throws Exception {
            String projectId = "test-project-id";

            doNothing().when(projectService).reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Reject a project with validation error")
        void testRejectProjectWithValidationError() throws Exception {
            String projectId = "test-project-id";

            doThrow(new ValidationException("Validation error")).when(projectService).reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Validation error"));
        }

        @Test
        @DisplayName("Reject a project with status failure")
        void testRejectProjectWithStatusFailure() throws Exception {
            String projectId = "test-project-id";

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectService)
                                                                                                                 .reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

        @Test
        @DisplayName("Issue a project")
        void testIssueProject() throws Exception {
            String projectId = "test-project-id";

            doNothing().when(projectService).issue(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/issue", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Issue a project with validation error")
        void testIssueProjectWithValidationError() throws Exception {
            String projectId = "test-project-id";

            doThrow(new ValidationException("Validation error")).when(projectService).issue(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/issue", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Validation error"));
        }

        @Test
        @DisplayName("Issue a project with status failure")
        void testIssueProjectWithStatusFailure() throws Exception {
            String projectId = "test-project-id";

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectService)
                                                                                                                 .issue(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/issue", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

    }

    @Nested
    @DisplayName("Investment API")
    class InvestmentAPI {

        @Test
        @DisplayName("Add investment")
        void testAddInvestment() throws Exception {
            String projectId = "test-project-id";
            AddInvestmentCommand addInvestmentCommand = new AddInvestmentCommand()
                    .bakerId("test-baker-id")
                    .amount(100.00);

            doNothing().when(projectService).addInvestment(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/investments", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(addInvestmentCommand)))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Confirm investment")
        void testConfirmInvestment() throws Exception {
            String projectId = "test-project-id";
            ConfirmInvestmentCommand confirmInvestmentCommand = new ConfirmInvestmentCommand()
                    .bakerId("test-baker-id")
                    .moneyTransferId("test-money-transfer-id");

            doNothing().when(projectService).confirmInvestment(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/investments/confirm", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(confirmInvestmentCommand)))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Cancel investment")
        void testCancelInvestment() throws Exception {
            String projectId = "test-project-id";
            CancelInvestmentCommand cancelInvestmentCommand = new CancelInvestmentCommand()
                    .bakerId("test-baker-id");

            doNothing().when(projectService).cancelInvestment(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/investments/cancel", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(cancelInvestmentCommand)))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Add investment with status failure")
        void testAddInvestmentWithStatusFailure() throws Exception {
            String projectId = "test-project-id";
            AddInvestmentCommand addInvestmentCommand = new AddInvestmentCommand()
                    .bakerId("test-baker-id")
                    .amount(100.00);

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectService)
                                                                                                                 .addInvestment(
                                                                                                                         eq(new ProjectId(projectId)),
                                                                                                                         any());

            mockMvc.perform(post("/admin/projects/{id}/investments", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(addInvestmentCommand)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

        @Test
        @DisplayName("Confirm investment with status failure")
        void testConfirmInvestmentWithStatusFailure() throws Exception {
            String projectId = "test-project-id";
            ConfirmInvestmentCommand confirmInvestmentCommand = new ConfirmInvestmentCommand()
                    .bakerId("test-baker-id")
                    .moneyTransferId("test-money-transfer-id");

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectService)
                                                                                                                 .confirmInvestment(
                                                                                                                         eq(new ProjectId(projectId)),
                                                                                                                         any());

            mockMvc.perform(post("/admin/projects/{id}/investments/confirm", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(confirmInvestmentCommand)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

        @Test
        @DisplayName("Cancel investment with status failure")
        void testCancelInvestmentWithStatusFailure() throws Exception {
            String projectId = "test-project-id";
            CancelInvestmentCommand cancelInvestmentCommand = new CancelInvestmentCommand()
                    .bakerId("test-baker-id");

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectService)
                                                                                                                 .cancelInvestment(
                                                                                                                         eq(new ProjectId(projectId)),
                                                                                                                         any());

            mockMvc.perform(post("/admin/projects/{id}/investments/cancel", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(cancelInvestmentCommand)))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

    }

    private SubmitCrowdfundingProjectCommand buildRandomSubmitCrowdfundingProjectCommand() {
        return new SubmitCrowdfundingProjectCommand()
                .title(TestUtils.getFaker().company().name())
                .description(TestUtils.getFaker().lorem().sentence(10))
                .requestedAmount(TestUtils.getRandomAmount().doubleValue())
                .currency("EUR")
                .projectStartDate(TestUtils.buildRandomOffsetDateTime())
                .projectEndDate(TestUtils.buildRandomOffsetDateTime())
                .owner(new nextcrowd.crowdfunding.admin.api.model.ProjectOwner()
                               .imageUrl(TestUtils.getFaker().internet().url())
                               .name(TestUtils.getFaker().lebowski().character()));
    }

}