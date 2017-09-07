import {inject} from 'aurelia-framework';
import EndpointsStore from '../store/EndpointsStore';

@inject(EndpointsStore)
export default class Apis {
  nb = 25;
  page = 1;
  datas = [];
  original = [];

  constructor(endpointsstore) {
    this.endpointsstore = endpointsstore;
  }

  activate() {
    const {nb, page} = this;
    this.endpointsstore.find({nb, page})
      .then(list => {
        let l = list.map(o => {
          const context = (o.artifactId || '').replace('-client', '');
          const absolutePath = `/${context}${o.path}`;
          return Object.assign({}, o, {absolutePath});
        });
        this.original = l;
        this.datas = l;
      });
  }
}
