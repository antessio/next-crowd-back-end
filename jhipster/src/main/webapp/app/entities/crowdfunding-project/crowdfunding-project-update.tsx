import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCrowdfundingProjectOwners } from 'app/entities/crowdfunding-project-owner/crowdfunding-project-owner.reducer';
import { createEntity, getEntity, updateEntity } from './crowdfunding-project.reducer';

export const CrowdfundingProjectUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const crowdfundingProjectOwners = useAppSelector(state => state.crowdfundingProjectOwner.entities);
  const crowdfundingProjectEntity = useAppSelector(state => state.crowdfundingProject.entity);
  const loading = useAppSelector(state => state.crowdfundingProject.loading);
  const updating = useAppSelector(state => state.crowdfundingProject.updating);
  const updateSuccess = useAppSelector(state => state.crowdfundingProject.updateSuccess);

  const handleClose = () => {
    navigate('/crowdfunding-project');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getCrowdfundingProjectOwners({}));
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
    if (values.requestedAmount !== undefined && typeof values.requestedAmount !== 'number') {
      values.requestedAmount = Number(values.requestedAmount);
    }
    if (values.collectedAmount !== undefined && typeof values.collectedAmount !== 'number') {
      values.collectedAmount = Number(values.collectedAmount);
    }
    if (values.risk !== undefined && typeof values.risk !== 'number') {
      values.risk = Number(values.risk);
    }
    values.projectStartDate = convertDateTimeToServer(values.projectStartDate);
    values.projectEndDate = convertDateTimeToServer(values.projectEndDate);
    if (values.numberOfBackers !== undefined && typeof values.numberOfBackers !== 'number') {
      values.numberOfBackers = Number(values.numberOfBackers);
    }
    if (values.expectedProfit !== undefined && typeof values.expectedProfit !== 'number') {
      values.expectedProfit = Number(values.expectedProfit);
    }
    if (values.minimumInvestment !== undefined && typeof values.minimumInvestment !== 'number') {
      values.minimumInvestment = Number(values.minimumInvestment);
    }

    const entity = {
      ...crowdfundingProjectEntity,
      ...values,
      owner: crowdfundingProjectOwners.find(it => it.id.toString() === values.owner?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          projectStartDate: displayDefaultDateTime(),
          projectEndDate: displayDefaultDateTime(),
        }
      : {
          ...crowdfundingProjectEntity,
          projectStartDate: convertDateTimeFromServer(crowdfundingProjectEntity.projectStartDate),
          projectEndDate: convertDateTimeFromServer(crowdfundingProjectEntity.projectEndDate),
          owner: crowdfundingProjectEntity?.owner?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nextCrowdBackEndApp.crowdfundingProject.home.createOrEditLabel" data-cy="CrowdfundingProjectCreateUpdateHeading">
            <Translate contentKey="nextCrowdBackEndApp.crowdfundingProject.home.createOrEditLabel">
              Create or edit a CrowdfundingProject
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
                  id="crowdfunding-project-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.title')}
                id="crowdfunding-project-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.requestedAmount')}
                id="crowdfunding-project-requestedAmount"
                name="requestedAmount"
                data-cy="requestedAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.collectedAmount')}
                id="crowdfunding-project-collectedAmount"
                name="collectedAmount"
                data-cy="collectedAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.currency')}
                id="crowdfunding-project-currency"
                name="currency"
                data-cy="currency"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.imageUrl')}
                id="crowdfunding-project-imageUrl"
                name="imageUrl"
                data-cy="imageUrl"
                type="text"
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.risk')}
                id="crowdfunding-project-risk"
                name="risk"
                data-cy="risk"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.projectStartDate')}
                id="crowdfunding-project-projectStartDate"
                name="projectStartDate"
                data-cy="projectStartDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.projectEndDate')}
                id="crowdfunding-project-projectEndDate"
                name="projectEndDate"
                data-cy="projectEndDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.numberOfBackers')}
                id="crowdfunding-project-numberOfBackers"
                name="numberOfBackers"
                data-cy="numberOfBackers"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.summary')}
                id="crowdfunding-project-summary"
                name="summary"
                data-cy="summary"
                type="text"
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.description')}
                id="crowdfunding-project-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.expectedProfit')}
                id="crowdfunding-project-expectedProfit"
                name="expectedProfit"
                data-cy="expectedProfit"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.minimumInvestment')}
                id="crowdfunding-project-minimumInvestment"
                name="minimumInvestment"
                data-cy="minimumInvestment"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.crowdfundingProject.projectVideoUrl')}
                id="crowdfunding-project-projectVideoUrl"
                name="projectVideoUrl"
                data-cy="projectVideoUrl"
                type="text"
              />
              <ValidatedField
                id="crowdfunding-project-owner"
                name="owner"
                data-cy="owner"
                label={translate('nextCrowdBackEndApp.crowdfundingProject.owner')}
                type="select"
              >
                <option value="" key="0" />
                {crowdfundingProjectOwners
                  ? crowdfundingProjectOwners.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/crowdfunding-project" replace color="info">
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

export default CrowdfundingProjectUpdate;
