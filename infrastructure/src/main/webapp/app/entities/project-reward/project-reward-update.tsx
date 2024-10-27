import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCrowdfundingProjects } from 'app/entities/crowdfunding-project/crowdfunding-project.reducer';
import { createEntity, getEntity, reset, updateEntity } from './project-reward.reducer';

export const ProjectRewardUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const crowdfundingProjects = useAppSelector(state => state.crowdfundingProject.entities);
  const projectRewardEntity = useAppSelector(state => state.projectReward.entity);
  const loading = useAppSelector(state => state.projectReward.loading);
  const updating = useAppSelector(state => state.projectReward.updating);
  const updateSuccess = useAppSelector(state => state.projectReward.updateSuccess);

  const handleClose = () => {
    navigate(`/project-reward${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCrowdfundingProjects({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...projectRewardEntity,
      ...values,
      crowdfundingProject: crowdfundingProjects.find(it => it.id.toString() === values.crowdfundingProject?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...projectRewardEntity,
          crowdfundingProject: projectRewardEntity?.crowdfundingProject?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nextCrowdBackEndApp.projectReward.home.createOrEditLabel" data-cy="ProjectRewardCreateUpdateHeading">
            <Translate contentKey="nextCrowdBackEndApp.projectReward.home.createOrEditLabel">Create or edit a ProjectReward</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="project-reward-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nextCrowdBackEndApp.projectReward.name')}
                id="project-reward-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.projectReward.imageUrl')}
                id="project-reward-imageUrl"
                name="imageUrl"
                data-cy="imageUrl"
                type="text"
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.projectReward.description')}
                id="project-reward-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                id="project-reward-crowdfundingProject"
                name="crowdfundingProject"
                data-cy="crowdfundingProject"
                label={translate('nextCrowdBackEndApp.projectReward.crowdfundingProject')}
                type="select"
              >
                <option value="" key="0" />
                {crowdfundingProjects
                  ? crowdfundingProjects.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/project-reward" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ProjectRewardUpdate;
