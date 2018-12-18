import {Module} from 'vuex';
import {getters} from './getters';
import {actions} from './actions';
import {mutations} from './mutations';
import {MicroServiceState, Project, Version} from './types';
import {RootState} from '../types';

export const state: MicroServiceState = {
  tables: [],
  apis: [],
  projects: [],
  project: {} as Project,
  versions: [] as Version[],
};

const namespaced: boolean = true;

const microservices: Module<MicroServiceState, RootState> = {
  namespaced,
  state,
  getters,
  actions,
  mutations
};
export default microservices;
