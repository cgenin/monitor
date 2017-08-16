import {inject} from 'aurelia-framework';
import {TablesStore} from '../store/TablesStore';
import {format} from '../Dates';

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
    if (!this.filter || this.filter.trim() === '') {
      this.list = this.original;
      return;
    }
    const upFilter = this.filter.toUpperCase();
    this.list = this.original
      .filter(p => {
        const data = JSON.stringify(Object.values(p)).toUpperCase();
        return data.indexOf(upFilter) !== -1;
      });
  }

  refresh() {
    this.loading = true;
    this.tablesStore.initialize()
      .then(res => {
        const l = res.map(o => {
          const latest = format(o.latestUpdate);
          const serviceStr = toServicesStr(o.services);
          return Object.assign({}, o, {latest, serviceStr});
        });
        this.list = l;
        this.original = l;
        this.filter = '';
        setTimeout(() => this.loading = false, 300);
      });
  }
}
