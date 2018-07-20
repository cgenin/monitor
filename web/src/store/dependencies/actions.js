import { initialize, getResources, reset, usedBy, namespace } from './constants';
import { SET_DEPENDENCIES, SET_RESOURCIES, UPDATE_DEPENDENCIES_BY_FIELD } from './mutations-type';

export default {
  [initialize]({ commit }) {
    commit(SET_RESOURCIES, []);
    commit(SET_DEPENDENCIES, {});
    return this.dispatch(`${namespace}/${getResources}`);
  },

  [getResources]({ commit }) {
    return fetch('/api/dependencies')
      .then(res => res.json())
      .then((content) => {
        commit(SET_RESOURCIES, content);
        return content;
      });
  },

  [reset]({ commit }) {
    commit(SET_DEPENDENCIES, {});
    return Promise.resolve({});
  },

  [usedBy]({ commit }, resource) {
    return fetch(`/api/dependencies/${resource}`)
      .then(res => res.json())
      .then((content) => {
        commit(UPDATE_DEPENDENCIES_BY_FIELD, { resource, content });
        return content;
      });
  },
};
