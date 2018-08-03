import { SET_RESUME } from './mutations-type';

export default {
  [SET_RESUME](state, resume) {
    state.resume = resume;
  },
};
