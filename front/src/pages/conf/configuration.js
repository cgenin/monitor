import {inject} from 'aurelia-framework';
import {MdToastService} from 'aurelia-materialize-bridge';
import {ConfigurationStore} from '../../store/ConfigurationStore';
import {getBaseUrl} from '../../store/Api';
import {success, error} from '../../Toasts';

@inject(ConfigurationStore, MdToastService)
export class Configuration {

  constructor(store, toast) {
    this.store = store;
    this.toast = toast;
    this.urlExport = `${getBaseUrl()}/api/configuration/db/export.json`;
    this.jsonImport = null;
  }

  fileSelected(evt) {
    const reader = new FileReader();
    const file = evt.target.files[0];
    reader.onload = (event) => {
      this.jsonImport = event.target.result;
    };
    reader.readAsText(file);
  }

  submitImport() {
    if (this.jsonImport) {
      this.store.importDb(this.jsonImport)
        .then(() =>  success(this.toast))
        .catch((err) => error(this.toast, err));

    }
  }
}
