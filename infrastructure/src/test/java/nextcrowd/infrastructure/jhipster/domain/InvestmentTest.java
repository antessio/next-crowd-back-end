package nextcrowd.infrastructure.jhipster.domain;

import static nextcrowd.infrastructure.jhipster.domain.CrowdfundingProjectTestSamples.*;
import static nextcrowd.infrastructure.jhipster.domain.InvestmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import nextcrowd.infrastructure.jhipster.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InvestmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Investment.class);
        Investment investment1 = getInvestmentSample1();
        Investment investment2 = new Investment();
        assertThat(investment1).isNotEqualTo(investment2);

        investment2.setId(investment1.getId());
        assertThat(investment1).isEqualTo(investment2);

        investment2 = getInvestmentSample2();
        assertThat(investment1).isNotEqualTo(investment2);
    }

    @Test
    void crowdfundingProjectTest() {
        Investment investment = getInvestmentRandomSampleGenerator();
        CrowdfundingProject crowdfundingProjectBack = getCrowdfundingProjectRandomSampleGenerator();

        investment.setCrowdfundingProject(crowdfundingProjectBack);
        assertThat(investment.getCrowdfundingProject()).isEqualTo(crowdfundingProjectBack);

        investment.crowdfundingProject(null);
        assertThat(investment.getCrowdfundingProject()).isNull();
    }
}
