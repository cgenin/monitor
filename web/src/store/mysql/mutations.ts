import {MutationTree} from 'vuex';
import {SET_INFO_SCHEMA, UPDATE_LOADING} from './mutation-type';
import {InfoSchema, MysqlState} from "./types";

export const mutations: MutationTree<MysqlState> = {

  [UPDATE_LOADING](state: MysqlState, loading: boolean) {
    state.testConnectionLoading = loading;
  },
  [SET_INFO_SCHEMA](state: MysqlState, infoSchema: InfoSchema[]) {
    state.infoSchemas = infoSchema;
  },
};

