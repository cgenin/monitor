import { SET_INFO_SCHEMA, UPDATE_LOADING } from './mutation-type';

export default {
  [UPDATE_LOADING](state, loading) {
    state.testConnectionLoading = loading;
  },
  [SET_INFO_SCHEMA](state, infoSchema) {
    state.infoSchema = infoSchema;
  },
};

