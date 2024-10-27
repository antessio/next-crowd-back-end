import projectOwner from 'app/entities/project-owner/project-owner.reducer';
import projectReward from 'app/entities/project-reward/project-reward.reducer';
import investment from 'app/entities/investment/investment.reducer';
import crowdfundingProject from 'app/entities/crowdfunding-project/crowdfunding-project.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  projectOwner,
  projectReward,
  investment,
  crowdfundingProject,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
