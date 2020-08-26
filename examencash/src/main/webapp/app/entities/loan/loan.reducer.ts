import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ILoan, defaultValue } from 'app/shared/model/loan.model';

export const ACTION_TYPES = {
  FETCH_LOAN_LIST: 'loan/FETCH_LOAN_LIST',
  FETCH_LOAN: 'loan/FETCH_LOAN',
  CREATE_LOAN: 'loan/CREATE_LOAN',
  UPDATE_LOAN: 'loan/UPDATE_LOAN',
  DELETE_LOAN: 'loan/DELETE_LOAN',
  RESET: 'loan/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ILoan>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

export type LoanState = Readonly<typeof initialState>;

// Reducer

export default (state: LoanState = initialState, action): LoanState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_LOAN_LIST):
    case REQUEST(ACTION_TYPES.FETCH_LOAN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_LOAN):
    case REQUEST(ACTION_TYPES.UPDATE_LOAN):
    case REQUEST(ACTION_TYPES.DELETE_LOAN):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_LOAN_LIST):
    case FAILURE(ACTION_TYPES.FETCH_LOAN):
    case FAILURE(ACTION_TYPES.CREATE_LOAN):
    case FAILURE(ACTION_TYPES.UPDATE_LOAN):
    case FAILURE(ACTION_TYPES.DELETE_LOAN):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_LOAN_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    case SUCCESS(ACTION_TYPES.FETCH_LOAN):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_LOAN):
    case SUCCESS(ACTION_TYPES.UPDATE_LOAN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_LOAN):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/loans';

// Actions

export const getEntities: ICrudGetAllAction<ILoan> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_LOAN_LIST,
    payload: axios.get<ILoan>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`),
  };
};

export const getEntity: ICrudGetAction<ILoan> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_LOAN,
    payload: axios.get<ILoan>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<ILoan> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_LOAN,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ILoan> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_LOAN,
    payload: axios.put(apiUrl, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ILoan> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_LOAN,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
