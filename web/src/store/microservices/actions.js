import { groupByProjects, loadTables, loadApis, loadProjects, getProject, getVersionsForProject } from './constants';
import { SET_APIS, SET_PROJECT, SET_PROJECTS, SET_TABLES, SET_VERSIONS } from './mutations-type';

const projects = () => fetch(`/api/projects?t=${new Date().getTime()}`)
    .then(res => res.json());

export default {
  [groupByProjects]() {
    return fetch('/api/tables/projects')
      .then(res => res.json());
  },
  [loadTables]({ commit }) {
    return fetch('/api/tables')
      .then(res => res.json())
      .then((content) => {
        commit(SET_TABLES, content);
        return content;
      });
  },
  [loadApis]({ commit }, { nb = 25, page = 1 }) {
    return fetch(`/api/endpoints?nb=${nb}&page=${page}`)
      .then(res => res.json())
      .then((content) => {
        commit(SET_APIS, content);
        return content;
      });
  },
  [loadProjects]({ commit }) {
    return projects()
      .then((content) => {
        commit(SET_PROJECTS, content);
        return content;
      });
  },
  [getProject]({ commit }, id) {
    return projects()
      .then((l) => {
        const project = l.find(p => p.id === id);
        commit(SET_PROJECT, project);
        return project;
      });
  },
  [getVersionsForProject]({ commit }, id) {
    return fetch(`/api/projects/${id}`)
      .then(res => res.json())
      .then((content) => {
        commit(SET_VERSIONS, content);
        return content;
      });
  },
};
