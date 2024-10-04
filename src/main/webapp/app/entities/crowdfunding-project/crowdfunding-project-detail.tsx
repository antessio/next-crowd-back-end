import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './crowdfunding-project.reducer';

export const CrowdfundingProjectDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const crowdfundingProjectEntity = useAppSelector(state => state.crowdfundingProject.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="crowdfundingProjectDetailsHeading">
          <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.detail.title">CrowdfundingProject</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.title">Title</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.title}</dd>
          <dt>
            <span id="requestedAmount">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.requestedAmount">Requested Amount</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.requestedAmount}</dd>
          <dt>
            <span id="collectedAmount">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.collectedAmount">Collected Amount</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.collectedAmount}</dd>
          <dt>
            <span id="currency">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.currency">Currency</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.currency}</dd>
          <dt>
            <span id="imageUrl">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.imageUrl">Image Url</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.imageUrl}</dd>
          <dt>
            <span id="risk">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.risk">Risk</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.risk}</dd>
          <dt>
            <span id="projectStartDate">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.projectStartDate">Project Start Date</Translate>
            </span>
          </dt>
          <dd>
            {crowdfundingProjectEntity.projectStartDate ? (
              <TextFormat value={crowdfundingProjectEntity.projectStartDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="projectEndDate">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.projectEndDate">Project End Date</Translate>
            </span>
          </dt>
          <dd>
            {crowdfundingProjectEntity.projectEndDate ? (
              <TextFormat value={crowdfundingProjectEntity.projectEndDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="numberOfBackers">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.numberOfBackers">Number Of Backers</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.numberOfBackers}</dd>
          <dt>
            <span id="summary">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.summary">Summary</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.summary}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.description">Description</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.description}</dd>
          <dt>
            <span id="expectedProfit">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.expectedProfit">Expected Profit</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.expectedProfit}</dd>
          <dt>
            <span id="minimumInvestment">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.minimumInvestment">Minimum Investment</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.minimumInvestment}</dd>
          <dt>
            <span id="projectVideoUrl">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.projectVideoUrl">Project Video Url</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectEntity.projectVideoUrl}</dd>
          <dt>
            <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.owner">Owner</Translate>
          </dt>
          <dd>{crowdfundingProjectEntity.owner ? crowdfundingProjectEntity.owner.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/crowdfunding-project" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/crowdfunding-project/${crowdfundingProjectEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CrowdfundingProjectDetail;
