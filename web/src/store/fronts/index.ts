import {Module} from 'vuex';
import {getters} from './getters';
import {actions} from './actions';
import {mutations} from './mutations';
import {FrontsState} from './types';
import {RootState} from '../types';

export const state: FrontsState = {
  resume: [],
  services: [],
  fronts: [],
};


const namespaced: boolean = true;

const fronts: Module<FrontsState, RootState> = {
  namespaced,
  state,
  getters,
  actions,
  mutations
};
export default fronts;
