let instance;

class ConfigurationStore {
  _

  constructor() {
    if (instance) {
      return instance;
    }
    this.state = {};
    instance = this;
  }

  get javaFilters() {
    return Array.from(this._state.javaFilters);
  }

  get npmFilters() {
    return Array.from(this._state.npmFilters);
  }

  importDb(data) {
    const body = JSON.stringify(data);

    return fetch('/api/configuration/db/import', {
      method: 'PUT',
      body
    });
  }

  initialize() {
    return fetch('/api/configuration')
      .then(res => res.json())
      .then(content => {
        this._state = content;
        return this.state;
      });
  }

  save(configuration) {
    const body = JSON.stringify(configuration);
    return fetch('/api/configuration', {
      method: 'PUT',
      body
    })
      .then(() => {
        this._state = configuration;
        return this.state;
      });
  }
}

export default new ConfigurationStore();
