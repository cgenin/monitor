import {inject} from 'aurelia-framework';
import {ProjectsStore} from '../../store/ProjectsStore';

@inject(ProjectsStore)
export class Welcome {

  nbProjects = 0;

  constructor(projectStore) {
    this.projectStore = projectStore;
  }

  activate() {
  this.projectStore.initialize().then(()=>this.nbProjects = this.projectStore.list.length)
  }
}
