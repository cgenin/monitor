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

  listen() {
    if (!this.isListening) {
      this.isListening = true;
      const eb = new EventBus('http://localhost:8280/eventbus', {server:''});
      eb.enableReconnect(true);
      eb.onopen = () => {
        console.log('eb on open');
        eb.registerHandler('console.text', (error, message) => {
          console.error(error);
          console.log('received a message: ' + JSON.stringify(message));
        });
      }
    }
  }
}

const consoleStore = new ConsoleStore();
consoleStore.listen();
export default consoleStore;
