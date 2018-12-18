export default function (store) {
  if (process.env.DEV && module.hot) {
    module.hot.accept(['./configuration'], () => {
      const newShowcase = require('./configuration').default;
      store.hotUpdate({ modules: { configuration: newShowcase } });
    });
    module.hot.accept(['./console'], () => {
      const newShowcase = require('./console').default;
      store.hotUpdate({ modules: { console: newShowcase } });
    });
    module.hot.accept(['./server'], () => {
      const newShowcase = require('./server').default;
      store.hotUpdate({ modules: { server: newShowcase } });
    });
    module.hot.accept(['./mysql'], () => {
      const newShowcase = require('./mysql').default;
      store.hotUpdate({ modules: { mysql: newShowcase } });
    });
    module.hot.accept(['./moniThor'], () => {
      const newShowcase = require('./moniThor').default;
      store.hotUpdate({ modules: { moniThor: newShowcase } });
    });
    module.hot.accept(['./microservices'], () => {
      const newShowcase = require('./microservices').default;
      store.hotUpdate({ modules: { microservices: newShowcase } });
    });
    module.hot.accept(['./dependencies'], () => {
      const newShowcase = require('./dependencies').default;
      store.hotUpdate({ modules: { dependencies: newShowcase } });
    });
    module.hot.accept(['./fronts'], () => {
      const newShowcase = require('./fronts').default;
      store.hotUpdate({ modules: { fronts: newShowcase } });
    });
  }
  return store;
};
