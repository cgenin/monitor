import ConfigurationStore from "./ConfigurationStore";

let instance;

class ServicesStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this._state = {
      services: null
    };
    this.moniThorUrl = null;
    ConfigurationStore.initialize()
      .then(() => {
        this.moniThorUrl = ConfigurationStore.moniThorUrl;
      });

    instance = this;
  }

  async getService(service) {
    if(this.moniThorUrl) {
      return await fetch(`${this.moniThorUrl}/api/services/ping?service=${service}`)
        .then((response) => response.json())
        .then((content) => {
          return content;
        })
    }
  }

}

export default new ServicesStore();
