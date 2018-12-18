import {GetterTree} from 'vuex';
import {ConsoleState, Message} from './types';
import {RootState} from '../types';

export const getters: GetterTree<ConsoleState, RootState> = {
  messages(state): Message[] {
    return state.messages;
  },
  listenning(state): boolean {
    return state.isListening;
  }
};
