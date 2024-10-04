import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CrowdfundingProjectOwner from './crowdfunding-project-owner';
import CrowdfundingProjectOwnerDetail from './crowdfunding-project-owner-detail';
import CrowdfundingProjectOwnerUpdate from './crowdfunding-project-owner-update';
import CrowdfundingProjectOwnerDeleteDialog from './crowdfunding-project-owner-delete-dialog';

const CrowdfundingProjectOwnerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CrowdfundingProjectOwner />} />
    <Route path="new" element={<CrowdfundingProjectOwnerUpdate />} />
    <Route path=":id">
      <Route index element={<CrowdfundingProjectOwnerDetail />} />
      <Route path="edit" element={<CrowdfundingProjectOwnerUpdate />} />
      <Route path="delete" element={<CrowdfundingProjectOwnerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CrowdfundingProjectOwnerRoutes;
