import {bindable, customElement, computedFrom, observable} from 'aurelia-framework';

const toColor = (method) => {
  switch (method) {
    case 'POST':
      return 'purple';
    case 'GET':
      return 'blue darken-2';
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
          return 'content_create';
        case 'GET':
          return 'unarchive';
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
