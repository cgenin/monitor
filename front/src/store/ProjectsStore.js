import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api';


export class ProjectsStore {
  constructor() {
    this.list = [];
    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  initialize() {
    return this.client.get('/api/projects')
      .then(res => {
        this.list = res.content;
        return this;
      });
  }
}
