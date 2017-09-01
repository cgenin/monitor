import {bindable} from 'aurelia-framework';

let INDEX = 1;

export class ChangelogButton {
  @bindable model = null;

  constructor() {
    this.modalId = `modal${INDEX++}`;
  }

  open() {
    this.modal.open();
  }
}
