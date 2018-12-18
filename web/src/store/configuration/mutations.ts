import {MutationTree} from 'vuex';
import { UPDATE } from './mutations-type';
import { ConfiguationState} from './types';

export const mutations: MutationTree<ConfiguationState> = {
  [UPDATE](state, configuration) {
    const { mysql, javaFilters, npmFilters, moniThorUrl } = configuration;
    state.mysql = mysql;
    state.javaFilters = javaFilters;
    state.npmFilters = npmFilters;
    state.moniThorUrl = moniThorUrl;
  },
};
