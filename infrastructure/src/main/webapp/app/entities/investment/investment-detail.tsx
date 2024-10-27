import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './investment.reducer';

export const InvestmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const investmentEntity = useAppSelector(state => state.investment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="investmentDetailsHeading">
          <Translate contentKey="nextCrowdBackEndApp.investment.detail.title">Investment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{investmentEntity.id}</dd>
          <dt>
            <span id="bakerId">
              <Translate contentKey="nextCrowdBackEndApp.investment.bakerId">Baker Id</Translate>
            </span>
          </dt>
          <dd>{investmentEntity.bakerId}</dd>
          <dt>
            <span id="amount">
              <Translate contentKey="nextCrowdBackEndApp.investment.amount">Amount</Translate>
            </span>
          </dt>
          <dd>{investmentEntity.amount}</dd>
          <dt>
            <span id="moneyTransferId">
              <Translate contentKey="nextCrowdBackEndApp.investment.moneyTransferId">Money Transfer Id</Translate>
            </span>
          </dt>
          <dd>{investmentEntity.moneyTransferId}</dd>
          <dt>
            <Translate contentKey="nextCrowdBackEndApp.investment.crowdfundingProject">Crowdfunding Project</Translate>
          </dt>
          <dd>{investmentEntity.crowdfundingProject ? investmentEntity.crowdfundingProject.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/investment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/investment/${investmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InvestmentDetail;
