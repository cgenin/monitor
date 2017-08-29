import {inject} from 'aurelia-framework';
import {ProjectsStore} from '../store/ProjectsStore';
import {formatYYYYMMDDHHmm} from '../Dates';

const depTootip = (attr) => {
  if (!attr || attr.length === 0) {
    return '<p>Aucune dépendance.</p>';
  }
  return attr.map(v => `<ul><li>${v}</li></ul>`).reduce((a, b) => a + b, '');
};


const map = (l) => {
  if (!l) {
    return [];
  }
  return l.map(p => {
    p.destinationUrl = `/projects/${p.id}`;
    p.snapshot = p.snapshot || '-';
    p.release = p.release || '-';
    p.javaDeps = p.javaDeps || [];
    p.npmDeps = p.npmDeps || [];
    p.tables = p.tables || [];
    p.npmDepsTootip = depTootip(p.npmDeps);
    p.latest = formatYYYYMMDDHHmm(p.latestUpdate);
    return p;
  });
};

@inject(ProjectsStore)
export class Projects {
  original = [];
  list = [];
  filter = '';
  modalOpt = {
    title: '',
    data: []
  };

  constructor(projectStore) {
    this.projectStore = projectStore;
  }

  filtering() {
    if (!this.filter || this.filter.trim() === '') {
      this.list = this.original;
      return;
    }
    const upFilter = this.filter.toUpperCase();
    this.list = this.original
      .filter(p => {
        const data = JSON.stringify(Object.values(p)).toUpperCase();
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

  openTables(p) {
    const data = p.tables.sort();
    const title = "Tables";
    this.modalOpt = {
      data, title
    };
    this.modal.open();
  }

  openJava(p) {
    const data = p.javaDeps.sort();
    const title = "Dépendance Java";
    this.modalOpt = {
      data, title
    };
    this.modal.open();
  }
}
