import { UPDATE } from './mutations-type';

export default {
  [UPDATE](state, configuration) {
    const { mysql, javaFilters, npmFilters, moniThorUrl } = configuration;
    state.mysql = mysql;
    state.javaFilters = javaFilters;
    state.npmFilters = npmFilters;
    state.moniThorUrl = moniThorUrl;
  },
};
