import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './project-reward.reducer';

export const ProjectRewardDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const projectRewardEntity = useAppSelector(state => state.projectReward.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectRewardDetailsHeading">
          <Translate contentKey="nextCrowdBackEndApp.projectReward.detail.title">ProjectReward</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectRewardEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="nextCrowdBackEndApp.projectReward.name">Name</Translate>
            </span>
          </dt>
          <dd>{projectRewardEntity.name}</dd>
          <dt>
            <span id="imageUrl">
              <Translate contentKey="nextCrowdBackEndApp.projectReward.imageUrl">Image Url</Translate>
            </span>
          </dt>
          <dd>{projectRewardEntity.imageUrl}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="nextCrowdBackEndApp.projectReward.description">Description</Translate>
            </span>
          </dt>
          <dd>{projectRewardEntity.description}</dd>
          <dt>
            <Translate contentKey="nextCrowdBackEndApp.projectReward.crowdfundingProject">Crowdfunding Project</Translate>
          </dt>
          <dd>{projectRewardEntity.crowdfundingProject ? projectRewardEntity.crowdfundingProject.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/project-reward" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-reward/${projectRewardEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectRewardDetail;
