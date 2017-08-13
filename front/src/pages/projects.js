import {inject} from 'aurelia-framework';
import {ProjectsStore} from '../store/ProjectsStore';

const depTootip = (attr) => {
  if (!attr || attr.length === 0) {
    return '<p>Aucune dépendance.</p>'
  }
  return attr.map(v => `<ul><li>${v}</li></ul>`).reduce((a, b) => a + b, '');
};

const format = (long) => {
  const date = new Date(long);
  const cMont = `${date.getMonth() + 1}`;
  const fMonth = (cMont.length === 1) ? `0${cMont}` : cMont;
  return `${date.getFullYear()}/${fMonth}/${date.getDate() + 1}`;
};

const map = (l) => {
  if (!l) {
    return [];
  }
  return l.map(p => {
    p.destinationUrl = `/projects/${p.id}`;
    p.snapshot =  p.snapshot || '-';
    p.release =  p.release || '-';
    p.javaDeps = p.javaDeps || [];
    p.npmDeps = p.npmDeps || [];
    p.tables = p.tables || [];
    p.javaDepsTootip = depTootip(p.javaDeps);
    p.npmDepsTootip = depTootip(p.npmDeps);
    p.tablesTootip = depTootip(p.tables);
    p.latest = format(p.latestUpdate);
    return p;
  });
};

@inject(ProjectsStore)
export class Projects {
  original = [];
  list = [];
  filter = '';

  htmlTooltip = `
  <table>
    <tr>
      <td><img src="http://aurelia-ui-toolkits.github.io/demo-materialize/images/chips-sample-1.jpg" /></td>
      <td>
        custom html with crappy table layout<br />
        but shows that it's working (I mean tables)
      </td>
    </tr>
  </table>
  `;

  constructor(projectStore) {
    this.projectStore = projectStore;

  }

  filtering() {
    console.log(this.filter)
    if (!this.filter || this.filter.trim() === '') {
      this.list = this.original;
      return;
    }
    const upFilter = this.filter.toUpperCase();
    this.list = this.original
      .filter(p => {
        const data = JSON.stringify(p).toUpperCase();
        return data.indexOf(upFilter) !== -1;
      });
  }

  activate() {
    this.refresh();
  }

  refresh() {
    this.projectStore.initialize()
      .then(() => {
        const res = map(this.projectStore.list);
        this.list = res;
        this.original = res;
        this.filter = '';
      });
  }
}