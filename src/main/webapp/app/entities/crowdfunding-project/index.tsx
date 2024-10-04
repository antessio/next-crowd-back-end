import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CrowdfundingProject from './crowdfunding-project';
import CrowdfundingProjectDetail from './crowdfunding-project-detail';
import CrowdfundingProjectUpdate from './crowdfunding-project-update';
import CrowdfundingProjectDeleteDialog from './crowdfunding-project-delete-dialog';

const CrowdfundingProjectRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CrowdfundingProject />} />
    <Route path="new" element={<CrowdfundingProjectUpdate />} />
    <Route path=":id">
      <Route index element={<CrowdfundingProjectDetail />} />
      <Route path="edit" element={<CrowdfundingProjectUpdate />} />
      <Route path="delete" element={<CrowdfundingProjectDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CrowdfundingProjectRoutes;
