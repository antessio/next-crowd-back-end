package nextcrowd.crowdfunding.infrastructure.api;

import static nextcrowd.crowdfunding.infrastructure.TestUtils.buildRandomInstant;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.net.URI;
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
import org.springframework.mock.web.MockMultipartFile;
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
import nextcrowd.crowdfunding.infrastructure.security.service.AuthenticationService;
import nextcrowd.crowdfunding.infrastructure.security.service.JwtService;
import nextcrowd.crowdfunding.infrastructure.storage.FileStorageService;
import nextcrowd.crowdfunding.project.ProjectServicePort;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectContent;
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
class AdminProjectControllerTest {

    private static final User ADMIN_USER = TestUtils.buildRandomUser(Set.of("ROLE_ADMIN"));
    private static final User APPLICATION_PROJECT_USER = TestUtils.buildRandomUser(Set.of("ROLE_PROJECT"));
    @MockBean
    private ProjectServicePort projectServicePort;
    @MockBean
    private FileStorageService fileStorageService;
    @MockBean
    private AuthenticationService authenticationService;

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
    @DisplayName("File upload")
    class TestUpload {

        @Test
        @DisplayName("Upload file")
        void testUploadFile() throws Exception {
            byte[] file = TestUtils.getFaker().lorem().sentence(10).getBytes();
            when(fileStorageService.storeFile(file, "text/plain")).thenReturn(URI.create(TestUtils.getFaker().internet().url()));
            MockMultipartFile mockFile = new MockMultipartFile(
                    "file",
                    "testfile.txt",
                    MediaType.TEXT_PLAIN_VALUE,
                    file
            );
            mockMvc.perform(multipart("/admin/upload")
                                    .file(mockFile)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.url").isString());
        }

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
            when(projectServicePort.getById(projectId)).thenReturn(Optional.of(project));

            // Act & Assert

            mockMvc.perform(get("/admin/projects/{id}", projectId.id())
                                    .header("Authorization", "Bearer " + jwtService.generateToken(APPLICATION_PROJECT_USER.getUsername())))
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
            ProjectContent projectContent = TestUtils.buildRandomProjectContent();
            ProjectId projectId = project.getId();
            when(projectServicePort.getById(projectId)).thenReturn(Optional.of(project));
            when(projectServicePort.getContentById(projectId)).thenReturn(Optional.of(projectContent));

            // Act & Assert

            mockMvc.perform(get("/admin/projects/{id}", projectId.id())
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername())))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").value(projectId.id()))
                   .andExpect(jsonPath("$.title").value(projectContent.getTitle()))
                   .andExpect(jsonPath("$.status").value(project.getStatus().toString()))
                   .andExpect(jsonPath("$.image.id").value(projectContent.getImage().getId().id()))
                   .andExpect(jsonPath("$.image.url").value(projectContent.getImage().getUrl()))
                   .andExpect(jsonPath("$.image.contentType").value(projectContent.getImage().getContentType()))
                   .andExpect(jsonPath("$.image.location").value(projectContent.getImage().getLocation().name()))
                   .andExpect(jsonPath("$.image.path").value(projectContent.getImage().getPath()))
                   .andExpect(jsonPath("$.currency").value(project.getCurrency()))
                   .andExpect(jsonPath("$.longDescription").value(projectContent.getLongDescription()))
                   .andExpect(jsonPath("$.video.id").value(projectContent.getVideo().getId().id()))
                   .andExpect(jsonPath("$.video.url").value(projectContent.getVideo().getUrl()))
                   .andExpect(jsonPath("$.video.contentType").value(projectContent.getVideo().getContentType()))
                   .andExpect(jsonPath("$.video.location").value(projectContent.getVideo().getLocation().name()))
                   .andExpect(jsonPath("$.video.path").value(projectContent.getVideo().getPath()))
                   .andExpect(jsonPath("$.description").value(projectContent.getDescription()))
                   .andExpect(jsonPath("$.requestedAmount").value(project.getRequestedAmount().doubleValue()))
                   .andExpect(jsonPath("$.collectedAmount").value(project.getCollectedAmount().orElseThrow().doubleValue()))
                   .andExpect(jsonPath("$.projectStartDate").value(project.getProjectStartDate().toString()))
                   .andExpect(jsonPath("$.projectEndDate").value(project.getProjectEndDate().toString()))
                   .andExpect(jsonPath("$.risk").value(project.getRisk().orElse(null)))
                   .andExpect(jsonPath("$.expectedProfit").value(project.getExpectedProfit().map(BigDecimal::doubleValue).orElse(null)))
                   .andExpect(jsonPath("$.minimumInvestment").value(project.getMinimumInvestment().map(BigDecimal::doubleValue).orElse(null)))
                   .andExpect(jsonPath("$.owner.id").value(project.getOwner().getId().id()))
                   .andExpect(jsonPath("$.owner.name").value(project.getOwner().getName()))
                   .andExpect(jsonPath("$.owner.image.id").value(projectContent.getOwner().getImage().getId().id()))
                   .andExpect(jsonPath("$.owner.image.url").value(projectContent.getOwner().getImage().getUrl()))
                   .andExpect(jsonPath("$.owner.image.contentType").value(projectContent.getOwner().getImage().getContentType()))
                   .andExpect(jsonPath("$.owner.image.location").value(projectContent.getOwner().getImage().getLocation().name()))
                   .andExpect(jsonPath("$.owner.image.path").value(projectContent.getOwner().getImage().getPath()))
                   .andExpect(jsonPath("$.numberOfBackers").value(project.getNumberOfBackers().orElseThrow()))
                   .andExpect(jsonPath("$.rewards").isArray())
                   .andExpect(jsonPath("$.rewards.length()").value(projectContent.getRewards().size()))
                   .andExpect(jsonPath("$.rewards[*].name").isArray())
                   .andExpect(jsonPath("$.rewards[*].image").isArray())
                   .andExpect(jsonPath("$.rewards[*].description").isArray());
        }

        @Test
        @DisplayName("By id - Not Found")
        void testGetProjectByIdNotFound() throws Exception {
            String projectId = "non-existent-project-id";

            when(projectServicePort.getById(new ProjectId(projectId))).thenReturn(Optional.empty());

            mockMvc.perform(get("/admin/projects/{id}", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername())))
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
            when(projectServicePort.getPendingReviewProjects(any())).thenReturn(projects.stream());

            mockMvc.perform(get("/admin/projects/pending-review")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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
            when(projectServicePort.getPublishedProjects(any())).thenReturn(projects.stream());

            mockMvc.perform(get("/admin/projects/published")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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
            when(projectServicePort.submitProject(any())).thenReturn(expectedProjectId);

            // Act & Assert
            mockMvc.perform(post("/admin/projects")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(submitCommandApi)))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").value(expectedProjectId.id()));
        }

        @Test
        @DisplayName("Submit a new project with validation error")
        void testSubmitProjectWithValidationError() throws Exception {
            SubmitCrowdfundingProjectCommand submitCommandApi = buildRandomSubmitCrowdfundingProjectCommand();
            when(projectServicePort.submitProject(any())).thenThrow(new ValidationException("Validation error"));

            mockMvc.perform(post("/admin/projects")
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doNothing().when(projectServicePort).approve(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/approve", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doThrow(new ValidationException("Validation error")).when(projectServicePort).approve(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/approve", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectServicePort)
                                                                                                                 .approve(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/approve", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doNothing().when(projectServicePort).reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Reject a project with validation error")
        void testRejectProjectWithValidationError() throws Exception {
            String projectId = "test-project-id";

            doThrow(new ValidationException("Validation error")).when(projectServicePort).reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Validation error"));
        }

        @Test
        @DisplayName("Reject a project with status failure")
        void testRejectProjectWithStatusFailure() throws Exception {
            String projectId = "test-project-id";

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectServicePort)
                                                                                                                 .reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Crowdfunding project exception "
                                                          + CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS.name()));
        }

        @Test
        @DisplayName("Issue a project")
        void testIssueProject() throws Exception {
            String projectId = "test-project-id";

            doNothing().when(projectServicePort).issue(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/issue", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Issue a project with validation error")
        void testIssueProjectWithValidationError() throws Exception {
            String projectId = "test-project-id";

            doThrow(new ValidationException("Validation error")).when(projectServicePort).issue(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/issue", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.message").value("Validation error"));
        }

        @Test
        @DisplayName("Issue a project with status failure")
        void testIssueProjectWithStatusFailure() throws Exception {
            String projectId = "test-project-id";

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectServicePort)
                                                                                                                 .issue(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/issue", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doNothing().when(projectServicePort).addInvestment(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/investments", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doNothing().when(projectServicePort).confirmInvestment(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/investments/confirm", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doNothing().when(projectServicePort).cancelInvestment(eq(new ProjectId(projectId)), any());

            mockMvc.perform(post("/admin/projects/{id}/investments/cancel", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectServicePort)
                                                                                                                 .addInvestment(
                                                                                                                         eq(new ProjectId(projectId)),
                                                                                                                         any());

            mockMvc.perform(post("/admin/projects/{id}/investments", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectServicePort)
                                                                                                                 .confirmInvestment(
                                                                                                                         eq(new ProjectId(projectId)),
                                                                                                                         any());

            mockMvc.perform(post("/admin/projects/{id}/investments/confirm", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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

            doThrow(new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS)).when(projectServicePort)
                                                                                                                 .cancelInvestment(
                                                                                                                         eq(new ProjectId(projectId)),
                                                                                                                         any());

            mockMvc.perform(post("/admin/projects/{id}/investments/cancel", projectId)
                                    .header("Authorization", "Bearer " + jwtService.generateToken(ADMIN_USER.getUsername()))
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
                               .image(new nextcrowd.crowdfunding.admin.api.model.UploadedResource()
                                              .id(TestUtils.getFaker().idNumber().valid())
                                              .url(URI.create(TestUtils.getFaker().internet().url()))
                                              .contentType(TestUtils.getFaker().internet().image()))
                               .name(TestUtils.getFaker().lebowski().character()));
    }


}