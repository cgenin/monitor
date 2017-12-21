let instance;

class MysqlStore {
  constructor() {
    if (instance) {
      return instance;
    }
    instance = this;
  }

  startOrStop() {
    return fetch('/api/configuration/db/mysql', {
      method: 'POST'
    });
  }

  createSchema() {
    return fetch('/api/configuration/db/mysql/schemas', {
      method: 'PUT'
    })
      .then(res => res.json());
  }
}

export default new MysqlStore();
