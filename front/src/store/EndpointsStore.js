import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api';

export default class EndpointsStore {
  constructor() {
    this.list = [];
    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  find({nb = 25, page = 1}) {
    return this.client.get(`/api/endpoints?nb=${nb}&page=${page}`)
      .then(res => {
        this.list = res.content;
        this.page = page;
        this.nb = nb;
        return this.list;
      });
  }
}
