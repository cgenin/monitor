/* eslint-disable global-require */
import Vue from 'vue';
import Vuex from 'vuex';

// we first import the module
import configuration from './configuration';
import console from './console';
import server from './server';
import mysql from './mysql';
import microservices from './microservices';
import dependencies from './dependencies';
import fronts from './fronts';
import moniThor from './moniThor';

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    // then we reference it
    configuration,
    console,
    server,
    mysql,
    microservices,
    moniThor,
    dependencies,
    fronts,
  },
});

// if we want some HMR magic for it, we handle
// the hot update like below. Notice we guard this
// code with "process.env.DEV" -- so this doesn't
// get into our production build (and it shouldn't).
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
  }); module.hot.accept(['./fronts'], () => {
    const newShowcase = require('./fronts').default;
    store.hotUpdate({ modules: { fronts: newShowcase } });
  });
}

export default store;
