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
        this.original = list;
        this.datas = list;
      });
  }
}
