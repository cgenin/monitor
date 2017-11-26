let instance;

class AppsStore {
  constructor() {
    if (instance) {
      return instance;
    }
    instance = this;
  }

  save(data) {
    const body = JSON.stringify(data);
    return fetch('/api/apps', {
      method: 'POST',
      body
    });
  }

  remove() {
    return fetch('/api/apps', {method: 'DELETE'});
  }
}

export default new AppsStore();
