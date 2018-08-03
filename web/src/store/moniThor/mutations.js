import { SET_CURRENT_SERVICE, SET_NPM_COMPONENT_INFOS, SET_NPM_LIST, SET_SERVERS, SET_URL } from './mutations-type';

export default {
  [SET_URL](state, url) {
    state.url = url;
  },
  [SET_SERVERS](state, servers) {
    state.servers = servers;
  },
  [SET_CURRENT_SERVICE](state, service) {
    state.service = service;
  },
  [SET_NPM_LIST](state, list) {
    state.npm.list = list;
  },
  [SET_NPM_COMPONENT_INFOS](state, { name, content }) {
    state.npm.byName[name] = content;
  },
};

