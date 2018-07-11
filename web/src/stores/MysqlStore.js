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

  migrateEvents() {
    return fetch('/api/configuration/db/events/store', {
      method: 'POST'
    })
      .then(res => res.json());
  }

  createSchema() {
    return fetch('/api/configuration/db/mysql/schemas', {
      method: 'PUT'
    })
      .then(res => res.json());
  }

  infoSchema() {
    return fetch('/api/configuration/db/mysql')
      .then(res => res.json());
  }

  test(mysql) {
    const body = JSON.stringify(mysql);

    return fetch('/api/configuration/db/mysql/connect', {
      method: 'POST',
      body
    })
      .then(res => res.json());
  }
}

export default new MysqlStore();
