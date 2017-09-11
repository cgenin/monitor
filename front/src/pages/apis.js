import {inject} from 'aurelia-framework';
import filtering from '../Filters'
import EndpointsStore from '../store/EndpointsStore';

const sortApis = (a, b) => {
  return a.absolutePath.localeCompare(b.absolutePath);
};

const extractQueryParams = (path) => {
  if (!path || path.indexOf('&') === -1) {
    return [];
  }
  return path.replace(/^(.*)\?/, '')
    .split('&');
};

@inject(EndpointsStore)
export default class Apis {
  nb = 25;
  filter = '';
  page = 1;
  datas = [];
  original = [];
  viewTable = true;

  constructor(endpointsstore) {
    this.endpointsstore = endpointsstore;
  }

  filtering() {
    this.datas = filtering(this.original, this.filter);
  }

  activate() {
    const {nb, page} = this;
    this.endpointsstore.find({nb, page})
      .then(list => {
        let l = list.map(o => {
          const context = (o.artifactId || '').replace('-client', '');
          const absolutePath = `/${context}${o.path}`
            .replace(/\?(.*)$/, '');
          const queryParams = extractQueryParams(`/${context}${o.path}`);
          return Object.assign({}, o, {absolutePath, queryParams});
        }).sort(sortApis);
        this.original = l;
        this.datas = l;
      });
  }
}
