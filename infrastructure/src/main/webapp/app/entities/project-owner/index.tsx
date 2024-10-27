import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProjectOwner from './project-owner';
import ProjectOwnerDetail from './project-owner-detail';
import ProjectOwnerUpdate from './project-owner-update';
import ProjectOwnerDeleteDialog from './project-owner-delete-dialog';

const ProjectOwnerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProjectOwner />} />
    <Route path="new" element={<ProjectOwnerUpdate />} />
    <Route path=":id">
      <Route index element={<ProjectOwnerDetail />} />
      <Route path="edit" element={<ProjectOwnerUpdate />} />
      <Route path="delete" element={<ProjectOwnerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProjectOwnerRoutes;
