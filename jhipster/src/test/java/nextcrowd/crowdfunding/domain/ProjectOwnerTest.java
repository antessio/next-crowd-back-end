package nextcrowd.crowdfunding.domain;

import static nextcrowd.crowdfunding.domain.CrowdfundingProjectOwnerTestSamples.*;
import static nextcrowd.crowdfunding.domain.CrowdfundingProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import nextcrowd.crowdfunding.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectOwnerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CrowdfundingProjectOwner.class);
        CrowdfundingProjectOwner crowdfundingProjectOwner1 = getCrowdfundingProjectOwnerSample1();
        CrowdfundingProjectOwner crowdfundingProjectOwner2 = new CrowdfundingProjectOwner();
        assertThat(crowdfundingProjectOwner1).isNotEqualTo(crowdfundingProjectOwner2);

        crowdfundingProjectOwner2.setId(crowdfundingProjectOwner1.getId());
        assertThat(crowdfundingProjectOwner1).isEqualTo(crowdfundingProjectOwner2);

        crowdfundingProjectOwner2 = getCrowdfundingProjectOwnerSample2();
        assertThat(crowdfundingProjectOwner1).isNotEqualTo(crowdfundingProjectOwner2);
    }

    @Test
    void projectTest() {
        CrowdfundingProjectOwner crowdfundingProjectOwner = getCrowdfundingProjectOwnerRandomSampleGenerator();
        CrowdfundingProject crowdfundingProjectBack = getCrowdfundingProjectRandomSampleGenerator();

        crowdfundingProjectOwner.addProject(crowdfundingProjectBack);
        assertThat(crowdfundingProjectOwner.getProjects()).containsOnly(crowdfundingProjectBack);
        assertThat(crowdfundingProjectBack.getOwner()).isEqualTo(crowdfundingProjectOwner);

        crowdfundingProjectOwner.removeProject(crowdfundingProjectBack);
        assertThat(crowdfundingProjectOwner.getProjects()).doesNotContain(crowdfundingProjectBack);
        assertThat(crowdfundingProjectBack.getOwner()).isNull();

        crowdfundingProjectOwner.projects(new HashSet<>(Set.of(crowdfundingProjectBack)));
        assertThat(crowdfundingProjectOwner.getProjects()).containsOnly(crowdfundingProjectBack);
        assertThat(crowdfundingProjectBack.getOwner()).isEqualTo(crowdfundingProjectOwner);

        crowdfundingProjectOwner.setProjects(new HashSet<>());
        assertThat(crowdfundingProjectOwner.getProjects()).doesNotContain(crowdfundingProjectBack);
        assertThat(crowdfundingProjectBack.getOwner()).isNull();
    }
}
