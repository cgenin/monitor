import {GetterTree} from 'vuex';
import {DependenciesState, ResourceDto} from "./types";
import {RootState} from "../types";

export const getters: GetterTree<DependenciesState, RootState> = {
  resources: state => state.resources.map(resource => new ResourceDto(resource.toLowerCase())),
  dependencies: state => state.dependencies,
};
