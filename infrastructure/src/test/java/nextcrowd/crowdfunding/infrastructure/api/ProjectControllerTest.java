package nextcrowd.crowdfunding.infrastructure.api;

import static nextcrowd.crowdfunding.infrastructure.TestUtils.buildRandomInstant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import nextcrowd.crowdfunding.admin.api.model.AddInvestmentCommand;
import nextcrowd.crowdfunding.admin.api.model.ApproveCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.admin.api.model.SubmitCrowdfundingProjectCommand;
import nextcrowd.crowdfunding.infrastructure.TestUtils;
import nextcrowd.crowdfunding.project.ProjectService;
import nextcrowd.crowdfunding.project.exception.ValidationException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.ProjectId;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;
    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Read Project API")
    class TestReadProjectAPI {

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
            mockMvc.perform(get("/admin/projects/{id}", projectId.id()))
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
                   .andExpect(jsonPath("$.projectEndDate").value(project.getProjectEndDate().toString()));
        }

        @Test
        @DisplayName("By id - Not Found")
        void testGetProjectByIdNotFound() throws Exception {
            String projectId = "non-existent-project-id";
            when(projectService.getById(new ProjectId(projectId))).thenReturn(Optional.empty());

            mockMvc.perform(get("/admin/projects/{id}", projectId))
                   .andExpect(status().isNotFound());
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
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(approveCommand)))
                   .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("Reject a project")
        void testRejectProject() throws Exception {
            String projectId = "test-project-id";

            doNothing().when(projectService).reject(eq(new ProjectId(projectId)));

            mockMvc.perform(post("/admin/projects/{id}/reject", projectId)
                                    .contentType(MediaType.APPLICATION_JSON))
                   .andExpect(status().isAccepted());
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
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(TestUtils.objectMapper().writeValueAsBytes(addInvestmentCommand)))
                   .andExpect(status().isAccepted());
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