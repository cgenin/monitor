import {inject} from 'aurelia-framework';
import {ProjectsStore} from '../../store/ProjectsStore';
import {formatYYYYMMDDHHmm} from '../../Dates';

@inject(ProjectsStore)
export default class ProjectDetail {
  id = 0;
  project = {};
  selected = {};
  versions = [];
  releases = [];
  snapshots = [];
  title = '';

  constructor(projectsstore) {
    this.projectsstore = projectsstore;
  }

  changeVersion(evt) {
    const id = evt.target.value;
    this.selected = this.versions.find(v => v.id === id);
  }

  activate(params) {
    this.id = params.id;
    Promise.all([this.projectsstore.getProject(this.id), this.projectsstore.getVersion(this.id)])
      .then(() => {
        this.project = this.projectsstore.project;
        this.title = `Détail du projet : ${this.project.name}`;
        this.versions = this.projectsstore.versions.map(o => {
          const latest = formatYYYYMMDDHHmm(o.latestUpdate);
          return Object.assign({}, o, {latest});
        });
        this.latest = this.versions
          .map(v => {
            v.table = v.tables.sort();
            v.javaDeps = v.javaDeps.sort();
            v.apis = v.apis.sort();
            v.icon=(v.isSnapshot) ? '/img/snapshot.svg': '/img/release.svg';
;            return v;
          })
          .reduce((a, b) => {
            if (a.latestUpdate > b.latestUpdate) {
              return a;
            }
            return b;
          });
        this.selected = this.latest;
        this.releases = this.versions.filter(v => !v.isSnapshot);
        this.snapshots = this.versions.filter(v => v.isSnapshot);
      })
      .catch(err => console.error(err));
  }
}
