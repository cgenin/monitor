import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api'

export class ConfigurationStore {
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
}
