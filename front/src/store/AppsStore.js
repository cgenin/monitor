import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api'

export class AppsStore {
  constructor() {

    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  save(data) {
    return this.client.post('/api/apps', data);
  }
}