import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api';


export class TablesStore {
  constructor() {
    this.list = [];
    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  initialize() {
    return this.client.get('/api/tables')
      .then(res => {
        this.list = res.content;
        return this.list;
      });
  }
}
