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


  clearDatas() {
    return fetch('/api/apps/calculate/datas', {method: 'DELETE'})
      .then(res => res.json());
  }
}

export default new AppsStore();
