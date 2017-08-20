import {inject} from 'aurelia-framework';
import {ConfigurationStore} from '../../store/ConfigurationStore';

@inject(ConfigurationStore)
export class Administration {
  javaFilterValue = '';
  javaFilters = [];

  constructor(configurationstore) {
    this.configurationstore = configurationstore;
  }

  delJavaFilters(v) {
    this.javaFilters = this.javaFilters.filter(f => f !== v);
  }

  addJavaFilters() {
    this.javaFilters.push(this.javaFilterValue);
    this.javaFilterValue = '';
  }

  save() {
    const {javaFilters} = this;
    const configuration = Object.assign({}, this.configurationstore.state, {javaFilters});
    this.configurationstore.save(configuration);
  }

  refresh() {
    const {javaFilters} = this.configurationstore.state;
    this.javaFilters = javaFilters;
  }

  attached() {
    this.configurationstore.initialize()
      .then(() => {
        this.refresh();
      })
      .catch(err => console.error(err));
  }
}
