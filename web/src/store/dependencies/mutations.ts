import {MutationTree} from 'vuex';
import {SET_DEPENDENCIES, SET_RESOURCIES, UPDATE_DEPENDENCIES_BY_FIELD} from './mutations-type';
import {DependenciesState} from "./types";

export const mutations: MutationTree<DependenciesState> = {
  [SET_DEPENDENCIES](state, deps) {
    state.dependencies = deps;
  },
  [UPDATE_DEPENDENCIES_BY_FIELD](state, {resource, content}) {
    state.dependencies[resource] = content;
  },
  [SET_RESOURCIES](state, resources) {
    state.resources = resources;
  },
};
