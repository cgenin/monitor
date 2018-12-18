import {Module} from 'vuex';
import {getters} from './getters';
import {actions} from './actions';
import {mutations} from './mutations';
import {ConsoleState} from './types';
import {RootState} from '../types';


export const state: ConsoleState = {
  messages: [],
  isListening: false
};

const namespaced: boolean = true;

const console: Module<ConsoleState, RootState> = {
  namespaced,
  state,
  getters,
  actions,
  mutations
};
export default console;
