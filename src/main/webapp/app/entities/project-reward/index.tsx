import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProjectReward from './project-reward';
import ProjectRewardDetail from './project-reward-detail';
import ProjectRewardUpdate from './project-reward-update';
import ProjectRewardDeleteDialog from './project-reward-delete-dialog';

const ProjectRewardRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProjectReward />} />
    <Route path="new" element={<ProjectRewardUpdate />} />
    <Route path=":id">
      <Route index element={<ProjectRewardDetail />} />
      <Route path="edit" element={<ProjectRewardUpdate />} />
      <Route path="delete" element={<ProjectRewardDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProjectRewardRoutes;
