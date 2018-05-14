import ConfigurationStore from "./ConfigurationStore";

let instance;

class NpmStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this._state = {
      byName: {},
      list: null
    };
    this.moniThorUrl = null;
    ConfigurationStore.initialize()
      .then(() => {
        this.moniThorUrl = ConfigurationStore.moniThorUrl;
      });

    instance = this;
  }

  async getComponentInfos(component) {
    return this._state.byName[component.name] || await this.initializeComponent(component);
  }

  async getList() {
    return this._state.list || await this.initialize();
  }

  async initializeComponent(component) {
    if (this.moniThorUrl) {
      return await fetch(`${this.moniThorUrl}/api/components/${component.name}`)
        .catch((e) => console.error(e))
        .then((response) => response.json())
        .then((content) => {
          this._state.byName[component.name] = content;
          return this._state.byName[component.name];
        });
    }
  }

  async initialize() {
    if(this.moniThorUrl) {
      return await fetch(`${this.moniThorUrl}/api/components/`)
        .catch((e) => console.error(e))
        .then((response) => response.json())
        .then((content) => {
          this._state.list = content;
          return this._state.list;
        });
    }
  }

}

export default new NpmStore();
