import {inject,useView } from 'aurelia-framework'
import {Router} from 'aurelia-router';

@inject(Router)
export class Layout {
  menu = [];
  sideNavCloseOnClick = false;

  constructor(router) {
    this.router = router;
  }

  attached() {
    this.menu = this.router.navigation.filter(r => r.config.menu);
  }
}