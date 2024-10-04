import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './crowdfunding-project-owner.reducer';

export const CrowdfundingProjectOwnerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const crowdfundingProjectOwnerEntity = useAppSelector(state => state.crowdfundingProjectOwner.entity);
  const loading = useAppSelector(state => state.crowdfundingProjectOwner.loading);
  const updating = useAppSelector(state => state.crowdfundingProjectOwner.updating);
  const updateSuccess = useAppSelector(state => state.crowdfundingProjectOwner.updateSuccess);

  const handleClose = () => {
    navigate('/crowdfunding-project-owner');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
      ...crowdfundingProjectOwnerEntity,
      ...values,
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
          ...crowdfundingProjectOwnerEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2
            id="nextCrowdBackEndApp.crowdfundingProjectOwner.home.createOrEditLabel"
            data-cy="CrowdfundingProjectOwnerCreateUpdateHeading"
          >
            <Translate contentKey="nextCrowdBackEndApp.crowdfundingProjectOwner.home.createOrEditLabel">
              Create or edit a CrowdfundingProjectOwner
            </Translate>
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
                  id="crowdfunding-project-owner-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProjectOwner.name')}
                id="crowdfunding-project-owner-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProjectOwner.imageUrl')}
                id="crowdfunding-project-owner-imageUrl"
                name="imageUrl"
                data-cy="imageUrl"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/crowdfunding-project-owner" replace color="info">
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

export default CrowdfundingProjectOwnerUpdate;
