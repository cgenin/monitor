import { SET_RESUME, SET_SERVICES } from './mutations-type';

export default {
  [SET_RESUME](state, resume) {
    state.resume = resume;
  },
  [SET_SERVICES](state, services) {
    state.services = services;
  },
};
