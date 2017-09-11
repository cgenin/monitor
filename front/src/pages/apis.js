import {inject} from 'aurelia-framework';
import EndpointsStore from '../store/EndpointsStore';

const sortApis = (a, b) => {
  return a.absolutePath.localeCompare(b.absolutePath) ;
};

@inject(EndpointsStore)
export default class Apis {
  nb = 25;
  filter='';
  page = 1;
  datas = [];
  original = [];
  viewTable = true;

  constructor(endpointsstore) {
    this.endpointsstore = endpointsstore;
  }

  filtering() {
    if (!this.filter || this.filter.trim() === '') {
      this.datas = this.original;
      return;
    }
    const upFilter = this.filter.toUpperCase();
    this.datas = this.original
      .filter(p => {

        const data = JSON.stringify(Object.values(p)).toUpperCase();
        return data.indexOf(upFilter) !== -1;
      });
  }

  activate() {
    const {nb, page} = this;
    this.endpointsstore.find({nb, page})
      .then(list => {
        let l = list.map(o => {
          const context = (o.artifactId || '').replace('-client', '');
          const absolutePath = `/${context}${o.path}`
            .replace(/\?(.*)$/, '');


          return Object.assign({}, o, {absolutePath});
        }).sort(sortApis);
        this.original = l;
        this.datas = l;
      });
  }
}
