import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './crowdfunding-project-owner.reducer';

export const CrowdfundingProjectOwnerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const crowdfundingProjectOwnerEntity = useAppSelector(state => state.crowdfundingProjectOwner.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="crowdfundingProjectOwnerDetailsHeading">
          <Translate contentKey="nextCrowdBackEndApp.crowdfundingProjectOwner.detail.title">CrowdfundingProjectOwner</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectOwnerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProjectOwner.name">Name</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectOwnerEntity.name}</dd>
          <dt>
            <span id="imageUrl">
              <Translate contentKey="nextCrowdBackEndApp.crowdfundingProjectOwner.imageUrl">Image Url</Translate>
            </span>
          </dt>
          <dd>{crowdfundingProjectOwnerEntity.imageUrl}</dd>
        </dl>
        <Button tag={Link} to="/crowdfunding-project-owner" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/crowdfunding-project-owner/${crowdfundingProjectOwnerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CrowdfundingProjectOwnerDetail;
