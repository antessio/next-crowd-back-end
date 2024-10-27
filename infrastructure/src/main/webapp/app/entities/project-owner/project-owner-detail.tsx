import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './project-owner.reducer';

export const ProjectOwnerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const projectOwnerEntity = useAppSelector(state => state.projectOwner.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="projectOwnerDetailsHeading">
          <Translate contentKey="nextCrowdBackEndApp.projectOwner.detail.title">ProjectOwner</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{projectOwnerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="nextCrowdBackEndApp.projectOwner.name">Name</Translate>
            </span>
          </dt>
          <dd>{projectOwnerEntity.name}</dd>
          <dt>
            <span id="imageUrl">
              <Translate contentKey="nextCrowdBackEndApp.projectOwner.imageUrl">Image Url</Translate>
            </span>
          </dt>
          <dd>{projectOwnerEntity.imageUrl}</dd>
        </dl>
        <Button tag={Link} to="/project-owner" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/project-owner/${projectOwnerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProjectOwnerDetail;
