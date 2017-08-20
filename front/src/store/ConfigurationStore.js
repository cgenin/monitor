import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api';

export class ConfigurationStore {
  state = {};

  constructor() {
    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  importDb(data) {
    return this.client.put('/api/configuration/db/import', data);
  }

  initialize() {
    return this.client.get('/api/configuration')
      .then(res => {
        this.state = res.content;
        return this.state;
      });
  }

  save(configuration) {
    return this.client.put('/api/configuration', configuration)
      .then(() => {
        this.state = configuration;
        return this.state;
      });
  }
}
