import {HttpClient} from 'aurelia-http-client';
import {getBaseUrl} from './Api';

export class ProjectsStore {
  constructor() {
    this.list = [];
    this.project = {};
    this.versions = [];
    this.client = new HttpClient()
      .configure(x => {
        x.withBaseUrl(getBaseUrl());
        x.withHeader('Content-type', 'application/json');
      });
  }

  initialize() {
    return this.client.get(`/api/projects?t=${new Date().getTime()}`)
      .then(res => {
        this.list = res.content;
        return this.list;
      });
  }

  getProject(id) {
    return this.initialize()
      .then(l => {
        this.project = l.find(p => p.id === id);
        return this.project;
      });
  }

  getVersion(id) {
    return this.client.get(`/api/projects/${id}`)
      .then(res => {
        this.versions = res.content;
        return this.versions;
      });
  }
}
