import {bindable, customElement, computedFrom} from 'aurelia-framework';

const toColor = (method) => {
  switch (method) {
    case 'POST':
      return 'purple';
    case 'GET':
      return 'blue darken-2';
    case 'DELETE':
      return 'red darken-2';
    case 'PUT':
      return 'green darken-2';
  }
  return '';
};


@customElement('method-icon')
export default class MethodIcon {
  @bindable method;


  @computedFrom('method')
  get icon() {
    if (this.method)
      switch (this.method) {
        case 'POST':
          return 'move_to_inbox';
        case 'GET':
          return 'unarchive';
        case 'DELETE':
          return 'delete_forever';
        case 'PUT':
          return 'add_box';
        default:
          console.error('method unkonwn' + this.method);
      }

    return '';
  }


  @computedFrom('method')
  get clazz() {
    return `icon center ${toColor(this.method)} white-text small`;
  }

}
