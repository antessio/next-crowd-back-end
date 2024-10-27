import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Investment from './investment';
import InvestmentDetail from './investment-detail';
import InvestmentUpdate from './investment-update';
import InvestmentDeleteDialog from './investment-delete-dialog';

const InvestmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Investment />} />
    <Route path="new" element={<InvestmentUpdate />} />
    <Route path=":id">
      <Route index element={<InvestmentDetail />} />
      <Route path="edit" element={<InvestmentUpdate />} />
      <Route path="delete" element={<InvestmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InvestmentRoutes;
