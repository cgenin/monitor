import {MutationTree} from 'vuex';
import {ConsoleState, Message} from './types';
import {ADD_MESSAGE, CLEAR, SET_LISTENNING} from './mutations-type';

export const mutations: MutationTree<ConsoleState> = {
  [ADD_MESSAGE](state, message: Message) {
    const dt = new Date(message.date);
    message.formattedDate = `${dt.getFullYear()}/${dt.getMonth() + 1}/${dt.getUTCDate()} ${dt.getHours()}:${dt.getMinutes()}:${dt.getSeconds()} ${dt.getMilliseconds()}`;
    message.key = `${dt.getTime()}-${state.messages.length}`;
    state.messages.push(message);
  },
  [CLEAR](state) {
    state.messages = [];
  },
  [SET_LISTENNING](state, listen) {
    state.isListening = listen;
  },
};
