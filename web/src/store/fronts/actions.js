import { loadResume, loadServices } from './constants';
import { SET_RESUME, SET_SERVICES } from './mutations-type';

export default {
  [loadResume]({ commit }) {
    return fetch('/api/fronts/groupby')
      .then(res => res.json())
      .then((content) => {
         commit(SET_RESUME, content);
         return content;
    });
  },
  [loadServices]({ commit }) {
    return fetch('/api/fronts/services')
      .then(res => res.json())
      .then((content) => {
        commit(SET_SERVICES, content);
        return content;
      });
  },
};
