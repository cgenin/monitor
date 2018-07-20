import { ADD_MESSAGE, CLEAR, SET_LISTENNING } from './mutations-type';

export default {
  [ADD_MESSAGE](state, message) {
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
