package nextcrowd.infrastructure.jhipster.domain;

import static nextcrowd.infrastructure.jhipster.domain.CrowdfundingProjectTestSamples.*;
import static nextcrowd.infrastructure.jhipster.domain.InvestmentTestSamples.*;
import static nextcrowd.infrastructure.jhipster.domain.ProjectOwnerTestSamples.*;
import static nextcrowd.infrastructure.jhipster.domain.ProjectRewardTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import nextcrowd.infrastructure.jhipster.web.rest.TestUtil;
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
    void ownerTest() {
        CrowdfundingProject crowdfundingProject = getCrowdfundingProjectRandomSampleGenerator();
        ProjectOwner projectOwnerBack = getProjectOwnerRandomSampleGenerator();

        crowdfundingProject.setOwner(projectOwnerBack);
        assertThat(crowdfundingProject.getOwner()).isEqualTo(projectOwnerBack);

        crowdfundingProject.owner(null);
        assertThat(crowdfundingProject.getOwner()).isNull();
    }

    @Test
    void rewardsTest() {
        CrowdfundingProject crowdfundingProject = getCrowdfundingProjectRandomSampleGenerator();
        ProjectReward projectRewardBack = getProjectRewardRandomSampleGenerator();

        crowdfundingProject.addRewards(projectRewardBack);
        assertThat(crowdfundingProject.getRewards()).containsOnly(projectRewardBack);
        assertThat(projectRewardBack.getCrowdfundingProject()).isEqualTo(crowdfundingProject);

        crowdfundingProject.removeRewards(projectRewardBack);
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
    void investmentsTest() {
        CrowdfundingProject crowdfundingProject = getCrowdfundingProjectRandomSampleGenerator();
        Investment investmentBack = getInvestmentRandomSampleGenerator();

        crowdfundingProject.addInvestments(investmentBack);
        assertThat(crowdfundingProject.getInvestments()).containsOnly(investmentBack);
        assertThat(investmentBack.getCrowdfundingProject()).isEqualTo(crowdfundingProject);

        crowdfundingProject.removeInvestments(investmentBack);
        assertThat(crowdfundingProject.getInvestments()).doesNotContain(investmentBack);
        assertThat(investmentBack.getCrowdfundingProject()).isNull();

        crowdfundingProject.investments(new HashSet<>(Set.of(investmentBack)));
        assertThat(crowdfundingProject.getInvestments()).containsOnly(investmentBack);
        assertThat(investmentBack.getCrowdfundingProject()).isEqualTo(crowdfundingProject);

        crowdfundingProject.setInvestments(new HashSet<>());
        assertThat(crowdfundingProject.getInvestments()).doesNotContain(investmentBack);
        assertThat(investmentBack.getCrowdfundingProject()).isNull();
    }
}
