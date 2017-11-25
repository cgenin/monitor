let instance;

class TablesStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this.list = [];
    instance = this;
  }

  initialize() {
    return fetch('/api/tables')
      .then((res) => res.json())
      .then(content => {
        this.list = content;
        return this.list;
      });
  }
}

const tablesStore = new TablesStore();
export default tablesStore
