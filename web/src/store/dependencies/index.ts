import {Module} from 'vuex';
import {getters} from './getters';
import {actions} from './actions';
import {mutations} from './mutations';
import {Dependencies, DependenciesState} from './types';
import {RootState} from '../types';

export const state: DependenciesState = {
  dependencies: {} as Dependencies,
  resources: [],
};

const namespaced: boolean = true;

const dependencies: Module<DependenciesState, RootState> = {
  namespaced,
  state,
  getters,
  actions,
  mutations
};
export default dependencies;
