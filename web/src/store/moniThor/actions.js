import { SET_CURRENT_SERVICE, SET_NPM_COMPONENT_INFOS, SET_NPM_LIST, SET_SERVERS } from './mutations-type';
import { getNpmComponentInfos, getServiceForServer, loadNpmList, loadServers, pingServer } from './constants';

export default {
  [getServiceForServer](context, { service, server }) {
    if (context.state.url) {
      return fetch(`${context.state.url}/api/services/ping?service=${service}&server=${server}`)
        .then(response => response.json())
        .then((content) => {
          context.commit(SET_CURRENT_SERVICE, { service, server, content });
          return content;
        });
    }
    return Promise.reject(new Error('no moniThorUrl'));
  },
  [getNpmComponentInfos]({ state, commit }, component) {
    const { name } = component;
    if (state.byName[name]) {
      return Promise.resolve(state.byName[component.name]);
    }
    return fetch(`${state.url}/api/components/${name}`)
      .catch(e => console.error(e))
      .then(response => response.json())
      .then((content) => {
        commit(SET_NPM_COMPONENT_INFOS, { name, content });
        return content;
      });
  },
  [loadNpmList]({ state, commit }) {
    if (state.url) {
      if (state.npm.list) {
        return Promise.resolve(state.npm.list);
      }
      const url = `${state.url}/api/components/`;
      return fetch(url)
        .catch(e => console.error(e))
        .then(response => response.json())
        .then((content) => {
          commit(SET_NPM_LIST, content);
          return content;
        })
        .catch((err) => {
          console.error(`*** Error in contacting monithor server : ${url} ***`);
          console.error(err);
        });
    }
    console.log('No monithor URL.');
    return Promise.resolve([]);
  },
  [loadServers]({ state, commit }) {
    return fetch(`${state.url}/api/servers`)
      .then(res => res.json())
      .then((serversWrappers) => {
        commit(SET_SERVERS, serversWrappers.servers);
      });
  },
  [pingServer]({ state }, { server }) {
    return fetch(`${state.url}/api/services/ping/all?server=${server}`)
      .then(res => res.json());
  },
};

