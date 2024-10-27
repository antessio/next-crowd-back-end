import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './crowdfunding-project.reducer';

export const CrowdfundingProject = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const crowdfundingProjectList = useAppSelector(state => state.crowdfundingProject.entities);
  const loading = useAppSelector(state => state.crowdfundingProject.loading);
  const totalItems = useAppSelector(state => state.crowdfundingProject.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="crowdfunding-project-heading" data-cy="CrowdfundingProjectHeading">
        <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.home.title">Crowdfunding Projects</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/crowdfunding-project/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.home.createLabel">Create new Crowdfunding Project</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {crowdfundingProjectList && crowdfundingProjectList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('requestedAmount')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.requestedAmount">Requested Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('requestedAmount')} />
                </th>
                <th className="hand" onClick={sort('collectedAmount')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.collectedAmount">Collected Amount</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('collectedAmount')} />
                </th>
                <th className="hand" onClick={sort('currency')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.currency">Currency</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('currency')} />
                </th>
                <th className="hand" onClick={sort('imageUrl')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.imageUrl">Image Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('imageUrl')} />
                </th>
                <th className="hand" onClick={sort('projectStartDate')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.projectStartDate">Project Start Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('projectStartDate')} />
                </th>
                <th className="hand" onClick={sort('projectEndDate')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.projectEndDate">Project End Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('projectEndDate')} />
                </th>
                <th className="hand" onClick={sort('numberOfBackers')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.numberOfBackers">Number Of Backers</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('numberOfBackers')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('longDescription')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.longDescription">Long Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('longDescription')} />
                </th>
                <th className="hand" onClick={sort('projectVideoUrl')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.projectVideoUrl">Project Video Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('projectVideoUrl')} />
                </th>
                <th className="hand" onClick={sort('risk')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.risk">Risk</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('risk')} />
                </th>
                <th className="hand" onClick={sort('expectedProfit')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.expectedProfit">Expected Profit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('expectedProfit')} />
                </th>
                <th className="hand" onClick={sort('minimumInvestment')}>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.minimumInvestment">Minimum Investment</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('minimumInvestment')} />
                </th>
                <th>
                  <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.owner">Owner</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {crowdfundingProjectList.map((crowdfundingProject, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/crowdfunding-project/${crowdfundingProject.id}`} color="link" size="sm">
                      {crowdfundingProject.id}
                    </Button>
                  </td>
                  <td>{crowdfundingProject.title}</td>
                  <td>
                    <Translate contentKey={`nextCrowdBackEndApp.Status.${crowdfundingProject.status}`} />
                  </td>
                  <td>{crowdfundingProject.requestedAmount}</td>
                  <td>{crowdfundingProject.collectedAmount}</td>
                  <td>{crowdfundingProject.currency}</td>
                  <td>{crowdfundingProject.imageUrl}</td>
                  <td>
                    {crowdfundingProject.projectStartDate ? (
                      <TextFormat type="date" value={crowdfundingProject.projectStartDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {crowdfundingProject.projectEndDate ? (
                      <TextFormat type="date" value={crowdfundingProject.projectEndDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{crowdfundingProject.numberOfBackers}</td>
                  <td>{crowdfundingProject.description}</td>
                  <td>{crowdfundingProject.longDescription}</td>
                  <td>{crowdfundingProject.projectVideoUrl}</td>
                  <td>{crowdfundingProject.risk}</td>
                  <td>{crowdfundingProject.expectedProfit}</td>
                  <td>{crowdfundingProject.minimumInvestment}</td>
                  <td>
                    {crowdfundingProject.owner ? (
                      <Link to={`/project-owner/${crowdfundingProject.owner.id}`}>{crowdfundingProject.owner.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/crowdfunding-project/${crowdfundingProject.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/crowdfunding-project/${crowdfundingProject.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/crowdfunding-project/${crowdfundingProject.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.home.notFound">No Crowdfunding Projects found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={crowdfundingProjectList && crowdfundingProjectList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default CrowdfundingProject;
