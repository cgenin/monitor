import {bindable, customElement, computedFrom} from 'aurelia-framework'

@customElement('card-api')
export default class CardApi {
  @bindable api;

  attached() {
    console.log(this.api.method)
  }

  @computedFrom('api')
  get parameters() {
    if (!this.api.params) {
      return [];
    }

    let parse = JSON.parse(this.api.params);
    console.log(parse);
    return parse;
  }
}
