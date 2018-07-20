import { SET_APIS, SET_PROJECT, SET_PROJECTS, SET_TABLES, SET_VERSIONS } from './mutations-type';

export default {
  [SET_TABLES](state, tables) {
    state.tables = tables;
  },
  [SET_APIS](state, apis) {
    state.apis = apis;
  },
  [SET_PROJECT](state, project) {
    state.project = project;
  },
  [SET_PROJECTS](state, projects) {
    state.projects = projects;
  },
  [SET_VERSIONS](state, versions) {
    state.versions = versions;
  },
};
