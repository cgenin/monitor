let instance;

class ConfigurationStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this._state = {};
    instance = this;
  }

  get mysql() {
    return this._state.mysql;
  }

  get javaFilters() {
    return Array.from(this._state.javaFilters);
  }

  get npmFilters() {
    return Array.from(this._state.npmFilters);
  }

  get moniThorUrl() {
    return this._state.moniThorUrl;
  }

  importDb(data) {
    const body = JSON.stringify(data);

    return fetch('/api/configuration/db/import', {
      method: 'PUT',
      body
    });
  }

  health() {
    return fetch('/api/_health')
      .then(res => res.json());
  }

  initialize() {
    return fetch('/api/configuration')
      .then(res => res.json())
      .then(content => {
        this._state = content;
        return this._state;
      });
  }

  save(configuration) {
    const body = JSON.stringify(configuration);
    return fetch('/api/configuration', {
      method: 'PUT',
      body
    })
      .then(() => {
        this.state = configuration;
        return this.state;
      });
  }
}

export default new ConfigurationStore();
