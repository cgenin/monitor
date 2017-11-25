let instance;
class ProjectsStore {
  constructor () {
    if (instance) {
      return instance;
    }
    instance = this;
    this.list = [];
    this.project = {};
    this.versions = [];
  }

  initialize () {
    return fetch(`/api/projects?t=${new Date().getTime()}`)
      .then(res => res.json()
      )
      .then((content) => {
        this.list = content;
        return this.list;
      })
  }

  getProject (id) {
    return this.initialize()
      .then((l) => {
        this.project = l.find(p => p.id === id)
        return this.project;
      })
  }

  getVersion (id) {
    return fetch(`/api/projects/${id}`)
      .then((res) => res.json())
      .then((content) => {
        this.versions = content;
        return this.versions;
      })
  }
}

const projectsStore = new ProjectsStore();
export default projectsStore;
