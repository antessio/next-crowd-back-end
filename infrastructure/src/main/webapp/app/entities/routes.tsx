import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProjectOwner from './project-owner';
import ProjectReward from './project-reward';
import Investment from './investment';
import CrowdfundingProject from './crowdfunding-project';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="project-owner/*" element={<ProjectOwner />} />
        <Route path="project-reward/*" element={<ProjectReward />} />
        <Route path="investment/*" element={<Investment />} />
        <Route path="crowdfunding-project/*" element={<CrowdfundingProject />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
