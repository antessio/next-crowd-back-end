import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCrowdfundingProjects } from 'app/entities/crowdfunding-project/crowdfunding-project.reducer';
import { createEntity, getEntity, reset, updateEntity } from './investment.reducer';

export const InvestmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const crowdfundingProjects = useAppSelector(state => state.crowdfundingProject.entities);
  const investmentEntity = useAppSelector(state => state.investment.entity);
  const loading = useAppSelector(state => state.investment.loading);
  const updating = useAppSelector(state => state.investment.updating);
  const updateSuccess = useAppSelector(state => state.investment.updateSuccess);

  const handleClose = () => {
    navigate(`/investment${location.search}`);
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
    if (values.amount !== undefined && typeof values.amount !== 'number') {
      values.amount = Number(values.amount);
    }

    const entity = {
      ...investmentEntity,
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
          ...investmentEntity,
          crowdfundingProject: investmentEntity?.crowdfundingProject?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nextCrowdBackEndApp.investment.home.createOrEditLabel" data-cy="InvestmentCreateUpdateHeading">
            <Translate contentKey="nextCrowdBackEndApp.investment.home.createOrEditLabel">Create or edit a Investment</Translate>
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
                  id="investment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nextCrowdBackEndApp.investment.bakerId')}
                id="investment-bakerId"
                name="bakerId"
                data-cy="bakerId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.investment.amount')}
                id="investment-amount"
                name="amount"
                data-cy="amount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nextCrowdBackEndApp.investment.moneyTransferId')}
                id="investment-moneyTransferId"
                name="moneyTransferId"
                data-cy="moneyTransferId"
                type="text"
              />
              <ValidatedField
                id="investment-crowdfundingProject"
                name="crowdfundingProject"
                data-cy="crowdfundingProject"
                label={translate('nextCrowdBackEndApp.investment.crowdfundingProject')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/investment" replace color="info">
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

export default InvestmentUpdate;
