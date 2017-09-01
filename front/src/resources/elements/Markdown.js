import {bindable, noView, customElement} from 'aurelia-framework';
import showdown from 'showdown';


function dedent(str) {
  const match = str.match(/^[ \t]*(?=\S)/gm);
  if (!match) return str;

  const indent = Math.min.apply(Math, match.map(function (el) {
    return el.length;
  }));

  const re = new RegExp('^[ \\t]{' + indent + '}', 'gm');
  return indent > 0 ? str.replace(re, '') : str;
}

@customElement("markdown")
@noView
export class Markdown {

  @bindable model = null;

  static inject = [Element];

  constructor(element) {
    this.element = element;
    this.converter = new showdown.Converter({simpleLineBreaks: true});
  }

  attached() {
    this.root = this.element.shadowRoot || this.element;
    if (!this.model) {
      this.valueChanged(this.element.innerHTML);
    } else {
      this.valueChanged(this.model);
    }
  }

  modelChanged() {
    this.valueChanged(this.model);
  }

  valueChanged(newValue) {
    if (!this.root) return;
    this.root.innerHTML = this.converter.makeHtml(dedent(newValue));
  }
}

