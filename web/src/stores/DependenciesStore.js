let instance;

class DependenciesStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this.resources = [];
    this.dependencies = {};
    instance = this;
  }

  initialize() {
    this.resources = [];
    this.resetDependencies();
    return this.getResources();
  }

  getResources() {
    return fetch('/api/dependencies')
      .then((res) => res.json())
      .then(content => {
        this.resources = content;
        return this.resources;
      });
  }

  resetDependencies() {
    this.dependencies = {};
  }

  usedBy(resource) {
    return fetch(`/api/dependencies/${resource}`)
      .then((res) => res.json())
      .then(content => {
        this.dependencies[resource] = content;
        return content;
      });
  }
}

const tablesStore = new DependenciesStore();
export default tablesStore
