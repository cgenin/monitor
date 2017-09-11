import {inject} from 'aurelia-framework';
import {TablesStore} from '../store/TablesStore';
import {format} from '../Dates';
import filtering from '../Filters';

const toServicesStr = (services) => {
  if (!services || services.length === 0) {
    return '';
  }
  if (services.length === 1) {
    return services[0];
  }

  return services.reduce((a, b) => a + ', ' + b);
};

@inject(TablesStore)
export default class Tables {
  original = [];
  list = [];
  filter = '';
  loading = false;


  constructor(tablesStore) {
    this.tablesStore = tablesStore;
  }

  activate() {
    this.refresh();
  }

  filtering() {
    this.list = filtering(this.original, this.filter);
  }

  refresh() {
    this.loading = true;
    this.tablesStore.initialize()
      .then(res => {
        const l = res.map(o => {
          const latest = format(o.latestUpdate);
          const serviceStr = toServicesStr(o.services);
          return Object.assign({}, o, {latest, serviceStr});
        }).sort((a, b) => {
          return a.name.localeCompare(b.name);
        });
        this.list = l;
        this.original = l;
        this.filter = '';
        setTimeout(() => this.loading = false, 300);
      });
  }
}
