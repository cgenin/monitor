let instance;

class EndpointsStore {
  constructor() {
    if (instance) {
      return instance;
    }
    this.list = [];
  }

  find({nb = 25, page = 1}) {
    return fetch(`/api/endpoints?nb=${nb}&page=${page}`)
      .then(res => res.json())
      .then(content => {
        this.list = content;
        this.page = page;
        this.nb = nb;
        return this.list;
      });
  }
}

export default new EndpointsStore();
