import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CrowdfundingProjectOwner from './crowdfunding-project-owner';
import CrowdfundingProject from './crowdfunding-project';
import ProjectReward from './project-reward';
import { Route } from 'react-router';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="crowdfunding-project-owner/*" element={<CrowdfundingProjectOwner />} />
        <Route path="crowdfunding-project/*" element={<CrowdfundingProject />} />
        <Route path="project-reward/*" element={<ProjectReward />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
