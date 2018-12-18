import {MutationTree} from 'vuex';
import { SET_RESUME, SET_SERVICES, SET_FRONTS } from './mutations-type';
import {FrontsState} from "./types";

export const mutations: MutationTree<FrontsState> = {
  [SET_RESUME](state, resume) {
    state.resume = resume;
  },
  [SET_SERVICES](state, services) {
    state.services = services;
  },
  [SET_FRONTS](state, fronts) {
    state.fronts = fronts;
  },
};
