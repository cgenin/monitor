import {inject} from 'aurelia-framework';
import {MdToastService} from 'aurelia-materialize-bridge';
import {AppsStore} from '../../store/AppsStore';
import {success, error} from '../../Toasts';

@inject(AppsStore, MdToastService)
export class ProjectsAdd {

  json = '';

  constructor(store, toast) {
    this.store = store;
    this.toast = toast;
  }

  submit() {
    this.store.save(this.json)
      .then(() =>  success(this.toast))
      .catch((err) => error(this.toast, err));
  }
}
