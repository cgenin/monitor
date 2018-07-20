import { UPDATE } from './mutations-type';
import { initialize, save } from './constants';

import { setUrlMutation } from '../moniThor/constants';

export default {
  [initialize]({ commit }) {
    return fetch('/api/configuration')
      .then(res => res.json())
      .then((content) => {
        commit(UPDATE, content);
        commit(setUrlMutation, content.moniThorUrl, { root: true });
      });
  },
  [save]({ commit }, configuration) {
    const body = JSON.stringify(configuration);
    return fetch('/api/configuration', {
      method: 'PUT',
      body,
    })
      .then(() => {
        commit(UPDATE, configuration);
        commit(setUrlMutation, configuration.moniThorUrl, { root: true });
        return configuration;
      });
  },
};
