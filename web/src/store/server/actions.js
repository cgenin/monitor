import { SET_DELETED_COLLECTION, UPDATE } from './mutations-type';
import { getHealth, importDb, clearDatas, remove } from './constants';

export default {
  [getHealth]({ commit }) {
    return fetch('/api/_health')
      .then(res => res.json())
      .then((data) => {
        commit(UPDATE, data);
      });
  },
  [importDb](context, data) {
    const body = JSON.stringify(data);
    return fetch('/api/configuration/db/import', {
      method: 'PUT',
      body,
    });
  },
  [remove]() {
    return fetch('/api/apps', { method: 'DELETE' });
  },
  [clearDatas]({ commit }) {
    commit(SET_DELETED_COLLECTION, []);
    return fetch('/api/apps/calculate/datas', { method: 'DELETE' })
      .then(res => res.json())
      .then((coll) => {
        commit(SET_DELETED_COLLECTION, coll);
        return coll;
      });
  },
};
