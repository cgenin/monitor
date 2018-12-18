import {Module} from 'vuex';
import {getters} from './getters';
import {actions} from './actions';
import {mutations} from './mutations';
import {MysqlState} from './types';
import {RootState} from '../types';

export const state: MysqlState = {
  testConnectionLoading: false,
  infoSchemas: [],
};


const namespaced: boolean = true;

const mysql: Module<MysqlState, RootState> = {
  namespaced,
  state,
  getters,
  actions,
  mutations
};
export default mysql;
