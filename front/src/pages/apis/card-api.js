import {bindable, customElement, computedFrom} from 'aurelia-framework'

@customElement('card-api')
export default class CardApis {
  @bindable api;

  attached(){
    console.log(this.api.method)
  }


}
