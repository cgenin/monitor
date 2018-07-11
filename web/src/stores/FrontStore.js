let instance;

class FrontStore {
  constructor() {
    if (instance) {
      return instance;
    }
    instance = this;
  }

  resume() {
    return fetch("/api/fronts/groupby")
      .then(res => res.json());
  }
}


export default new FrontStore();
