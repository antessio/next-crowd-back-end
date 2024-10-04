package nextcrowd.crowdfunding.domain;

import static nextcrowd.crowdfunding.domain.CrowdfundingProjectOwnerTestSamples.*;
import static nextcrowd.crowdfunding.domain.CrowdfundingProjectTestSamples.*;
import static nextcrowd.crowdfunding.domain.ProjectRewardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import nextcrowd.crowdfunding.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CrowdfundingProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CrowdfundingProject.class);
        CrowdfundingProject crowdfundingProject1 = getCrowdfundingProjectSample1();
        CrowdfundingProject crowdfundingProject2 = new CrowdfundingProject();
        assertThat(crowdfundingProject1).isNotEqualTo(crowdfundingProject2);

        crowdfundingProject2.setId(crowdfundingProject1.getId());
        assertThat(crowdfundingProject1).isEqualTo(crowdfundingProject2);

        crowdfundingProject2 = getCrowdfundingProjectSample2();
        assertThat(crowdfundingProject1).isNotEqualTo(crowdfundingProject2);
    }

    @Test
    void rewardTest() {
        CrowdfundingProject crowdfundingProject = getCrowdfundingProjectRandomSampleGenerator();
        ProjectReward projectRewardBack = getProjectRewardRandomSampleGenerator();

        crowdfundingProject.addReward(projectRewardBack);
        assertThat(crowdfundingProject.getRewards()).containsOnly(projectRewardBack);
        assertThat(projectRewardBack.getCrowdfundingProject()).isEqualTo(crowdfundingProject);

        crowdfundingProject.removeReward(projectRewardBack);
        assertThat(crowdfundingProject.getRewards()).doesNotContain(projectRewardBack);
        assertThat(projectRewardBack.getCrowdfundingProject()).isNull();

        crowdfundingProject.rewards(new HashSet<>(Set.of(projectRewardBack)));
        assertThat(crowdfundingProject.getRewards()).containsOnly(projectRewardBack);
        assertThat(projectRewardBack.getCrowdfundingProject()).isEqualTo(crowdfundingProject);

        crowdfundingProject.setRewards(new HashSet<>());
        assertThat(crowdfundingProject.getRewards()).doesNotContain(projectRewardBack);
        assertThat(projectRewardBack.getCrowdfundingProject()).isNull();
    }

    @Test
    void ownerTest() {
        CrowdfundingProject crowdfundingProject = getCrowdfundingProjectRandomSampleGenerator();
        CrowdfundingProjectOwner crowdfundingProjectOwnerBack = getCrowdfundingProjectOwnerRandomSampleGenerator();

        crowdfundingProject.setOwner(crowdfundingProjectOwnerBack);
        assertThat(crowdfundingProject.getOwner()).isEqualTo(crowdfundingProjectOwnerBack);

        crowdfundingProject.owner(null);
        assertThat(crowdfundingProject.getOwner()).isNull();
    }
}
