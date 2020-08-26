import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Loan from './loan';
import LoanDetail from './loan-detail';
import LoanUpdate from './loan-update';
import LoanDeleteDialog from './loan-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={LoanUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={LoanUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={LoanDetail} />
      <ErrorBoundaryRoute path={match.url} component={Loan} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={LoanDeleteDialog} />
  </>
);

export default Routes;
