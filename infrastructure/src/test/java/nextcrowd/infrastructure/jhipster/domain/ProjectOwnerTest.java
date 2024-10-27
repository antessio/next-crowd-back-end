package nextcrowd.infrastructure.jhipster.domain;

import static nextcrowd.infrastructure.jhipster.domain.CrowdfundingProjectTestSamples.*;
import static nextcrowd.infrastructure.jhipster.domain.ProjectOwnerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import nextcrowd.infrastructure.jhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectOwnerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectOwner.class);
        ProjectOwner projectOwner1 = getProjectOwnerSample1();
        ProjectOwner projectOwner2 = new ProjectOwner();
        assertThat(projectOwner1).isNotEqualTo(projectOwner2);

        projectOwner2.setId(projectOwner1.getId());
        assertThat(projectOwner1).isEqualTo(projectOwner2);

        projectOwner2 = getProjectOwnerSample2();
        assertThat(projectOwner1).isNotEqualTo(projectOwner2);
    }

    @Test
    void crowdfundingProjectTest() {
        ProjectOwner projectOwner = getProjectOwnerRandomSampleGenerator();
        CrowdfundingProject crowdfundingProjectBack = getCrowdfundingProjectRandomSampleGenerator();

        projectOwner.setCrowdfundingProject(crowdfundingProjectBack);
        assertThat(projectOwner.getCrowdfundingProject()).isEqualTo(crowdfundingProjectBack);
        assertThat(crowdfundingProjectBack.getOwner()).isEqualTo(projectOwner);

        projectOwner.crowdfundingProject(null);
        assertThat(projectOwner.getCrowdfundingProject()).isNull();
        assertThat(crowdfundingProjectBack.getOwner()).isNull();
    }
}
