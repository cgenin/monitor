import {customAttribute, DOM, inject} from 'aurelia-framework';

@customAttribute('auto-resize')
@inject(DOM.Element)
export class AutoResizer {
  constructor(element) {
    this.element = element;
    console.log(element.rows)
  }

  valueChanged(newValue, oldValue) {
    if (this.element.tagName === 'TEXTAREA') {
      const matches = newValue.match(/\n/g);
      const breaks = matches ? matches.length : 2;
      const rows = breaks + 2;
      this.element.rows = rows;
      this.element.style.height = `${rows}rem`;
      this.element.style.overflowY = (rows > 5) ? 'auto' : 'hidden';
    } else {
      this.logger.debug('Only textarea element managed : ' + this.element.tagName)
    }

  }
}