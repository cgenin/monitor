import {GetterTree} from 'vuex';
import {ConfiguationState} from './types';
import {RootState} from '../types';

export const getters: GetterTree<ConfiguationState, RootState> = {
  mysql: state => state.mysql,
  javaFilters: state => state.javaFilters,
  npmFilters: state => state.npmFilters,
  moniThorUrl: state => state.moniThorUrl,
  global: state => state,
};
