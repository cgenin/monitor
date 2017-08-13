import {inject} from 'aurelia-framework';
import {MdToastService} from 'aurelia-materialize-bridge';
import {AppsStore} from '../../store/AppsStore';

@inject(AppsStore, MdToastService)
export class ProjectsAdd {

  json = '';

  constructor(store, toast) {
    this.store = store;
    this.toast = toast;
  }

  submit() {
    this.store.save(this.json).then(() => {
      console.log('ok');
      this.toast.show("Mise à jour effectuer avec succés. :)", 4000, 'green');
    }).catch(() => {
      console.error('error');
      this.toast.show("Erreur technique !!! :(", 4000, 'red');
    });
  }
}