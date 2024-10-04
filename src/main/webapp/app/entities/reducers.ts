import crowdfundingProjectOwner from 'app/entities/crowdfunding-project-owner/crowdfunding-project-owner.reducer';
import crowdfundingProject from 'app/entities/crowdfunding-project/crowdfunding-project.reducer';
import projectReward from 'app/entities/project-reward/project-reward.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  crowdfundingProjectOwner,
  crowdfundingProject,
  projectReward,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
