import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api';

export class ConfigurationStore {
  _state = {};

  constructor() {
    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  get javaFilters() {
    return Array.from(this._state.javaFilters);
  }

  get npmFilters() {
    return Array.from(this._state.npmFilters);
  }

  importDb(data) {
    return this.client.put('/api/configuration/db/import', data);
  }

  initialize() {
    return this.client.get('/api/configuration')
      .then(res => {
        this._state = res.content;
        return this.state;
      });
  }

  save(configuration) {
    return this.client.put('/api/configuration', configuration)
      .then(() => {
        this._state = configuration;
        return this.state;
      });
  }
}
