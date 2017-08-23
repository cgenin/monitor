import {inject} from 'aurelia-framework';
import {ConfigurationStore} from '../../store/ConfigurationStore';
import {MdToastService} from 'aurelia-materialize-bridge';
import {success, error} from '../../Toasts';

@inject(ConfigurationStore, MdToastService)
export class Administration {
  javaFilterValue = '';
  javaFilters = [];

  npmFilterValue = '';
  npmFilters = [];

  constructor(configurationstore, toast) {
    this.configurationstore = configurationstore;
    this.toast = toast;
  }

  delJavaFilters(v) {
    this.javaFilters = this.javaFilters.filter(f => f !== v);
  }

  delNpmFilters(v) {
    this.npmFilters = this.npmFilters.filter(f => f !== v);
  }

  addJavaFilters() {
    this.javaFilters.push(this.javaFilterValue);
    this.javaFilterValue = '';
  }

  addNpmFilters() {
    this.npmFilters.push(this.npmFilterValue);
    this.npmFilterValue = '';
  }

  save() {
    const {javaFilters, npmFilters} = this;
    const configuration = Object.assign({}, this.configurationstore.state, {javaFilters, npmFilters});
    this.configurationstore.save(configuration)
      .then(() => success(this.toast))
      .catch((err) => error(this.toast, err));
  }

  refresh() {
    const {javaFilters, npmFilters} = this.configurationstore;
    this.javaFilters = javaFilters;
    this.npmFilters = npmFilters;
  }

  attached() {
    this.configurationstore.initialize()
      .then(() => {
        this.refresh();
      })
      .catch(err => console.error(err));
  }
}
