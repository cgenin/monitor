import EventBus from 'vertx3-eventbus-client'

let instance;

class ConsoleStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this.state = [];
    this.isListening = false;
    instance = this;
  }

  clear() {
    this.state = [];
  }

  addMessage(body) {
    const dt = new Date(body.date);
    body.formattedDate = `${dt.getFullYear()}/${dt.getMonth() + 1}/${dt.getUTCDate()} ${dt.getHours()}:${dt.getMinutes()}:${dt.getSeconds()} ${dt.getMilliseconds()}`;
    this.state.push(body);
  }

  listen() {
    if (!this.isListening) {
      this.isListening = true;
      const eb = new EventBus('http://localhost:8279/eventbus', {server: ''});
      eb.enableReconnect(true);
      eb.onopen = () => {
        this.addMessage({date: new Date().getTime(), msg: 'Console connecté'});
        eb.registerHandler('console.text', (error, message) => {
          if (error) {
            console.error(error);
            return;
          }
          const {body} = message;
          this.addMessage(body);
        });
      }
    }
  }
}

const consoleStore = new ConsoleStore();
consoleStore.listen();
export default consoleStore;
