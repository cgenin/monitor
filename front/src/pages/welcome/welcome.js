import {inject} from 'aurelia-framework';
import {ProjectsStore} from '../../store/ProjectsStore';
import {TablesStore} from '../../store/TablesStore';

@inject(ProjectsStore, TablesStore)
export class Welcome {

  nbProjects = 0;
  nbTables = 0;

  constructor(projectStore, tableStore) {
    this.projectStore = projectStore;
    this.tableStore = tableStore;
  }

  activate() {
    this.projectStore.initialize().then(() => this.nbProjects = this.projectStore.list.length);
    this.tableStore.initialize().then(() => this.nbTables = this.tableStore.list.length);
  }
}
