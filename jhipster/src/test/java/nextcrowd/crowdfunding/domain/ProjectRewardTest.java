package nextcrowd.crowdfunding.domain;

import static nextcrowd.crowdfunding.domain.CrowdfundingProjectTestSamples.*;
import static nextcrowd.crowdfunding.domain.ProjectRewardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import nextcrowd.crowdfunding.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectRewardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectReward.class);
        ProjectReward projectReward1 = getProjectRewardSample1();
        ProjectReward projectReward2 = new ProjectReward();
        assertThat(projectReward1).isNotEqualTo(projectReward2);

        projectReward2.setId(projectReward1.getId());
        assertThat(projectReward1).isEqualTo(projectReward2);

        projectReward2 = getProjectRewardSample2();
        assertThat(projectReward1).isNotEqualTo(projectReward2);
    }

    @Test
    void crowdfundingProjectTest() {
        ProjectReward projectReward = getProjectRewardRandomSampleGenerator();
        CrowdfundingProject crowdfundingProjectBack = getCrowdfundingProjectRandomSampleGenerator();

        projectReward.setCrowdfundingProject(crowdfundingProjectBack);
        assertThat(projectReward.getCrowdfundingProject()).isEqualTo(crowdfundingProjectBack);

        projectReward.crowdfundingProject(null);
        assertThat(projectReward.getCrowdfundingProject()).isNull();
    }
}
