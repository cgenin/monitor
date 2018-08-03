import { loadResume } from './constants';
import { SET_RESUME } from './mutations-type';

export default {
  [loadResume]({ commit }) {
    return fetch('/api/fronts/groupby')
      .then(res => res.json())
      .then((content) => {
         commit(SET_RESUME, content);
         return content;
    });
  },
};
