import {MutationTree} from 'vuex';
import {Api, MicroServiceState, Project, Table, Version} from './types';
import {SET_APIS, SET_PROJECT, SET_PROJECTS, SET_TABLES, SET_VERSIONS} from './mutations-type';


export const mutations: MutationTree<MicroServiceState> = {
  [SET_TABLES](state, tables: Table[]) {
    state.tables = tables;
  },
  [SET_APIS](state, apis: Api[]) {
    state.apis = apis;
  },
  [SET_PROJECT](state, project: Project) {
    state.project = project;
  },
  [SET_PROJECTS](state, projects: Project[]) {
    state.projects = projects;
  },
  [SET_VERSIONS](state, versions: Version[]) {
    state.versions = versions;
  },
};
