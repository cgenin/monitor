import {ActionTree} from 'vuex';
import { loadResume, loadServices, findByService, reset } from './constants';
import { SET_RESUME, SET_SERVICES, SET_FRONTS } from './mutations-type';
import {FrontsState} from "./types";
import {RootState} from "../types";

export const actions: ActionTree<FrontsState, RootState> = {

  [loadResume]({ commit }) {
    return fetch('/api/fronts/groupby')
      .then(res => res.json())
      .then((content) => {
         commit(SET_RESUME, content);
         return content;
    });
  },
  [loadServices]({ commit }) {
    return fetch('/api/fronts/services')
      .then(res => res.json())
      .then((content) => {
        commit(SET_SERVICES, content);
        return content;
      });
  },
  [reset]({ commit }) {
    commit(SET_FRONTS, []);
    return Promise.resolve({});
  },
  [findByService]({ commit }, service) {
    return fetch(`/api/fronts/by/${service}`)
      .then(res => res.json())
      .then((content) => {
        commit(SET_FRONTS, content);
        return content;
      });
  },
};
