import Vue from 'vue';
import VueRouter from 'vue-router';
import 'moment/locale/fr';
import 'moment/locale/en-gb';

Vue.use(VueRouter);

// DO NOT import the store here as you will receive it as
// parameter in the default exported function:

export default function (/* { store } */) {
  // IMPORTANT! Instantiate Router inside this function

  const r = new VueRouter({
    /*
     * NOTE! VueRouter "history" mode DOESN'T works for Cordova builds,
     * it is only to be used only for websites.
     *
     * If you decide to go with "history" mode, please also open /config/index.js
     * and set "build.publicPath" to something other than an empty string.
     * Example: '/' instead of current ''
     *
     * If switching back to default "hash" mode, don't forget to set the
     * build publicPath back to '' so Cordova builds work again.
     */

    mode: 'hash',
    scrollBehavior: () => ({ y: 0 }),

    routes: [
      { name: 'welcome', path: '/', component: () => import(/* webpackChunkName: "welcome" */ '../pages/Welcome.vue') },
      {
        name: 'projectsList',
        path: '/projects-list',
        component: () => import(/* webpackChunkName: "project-list" */ '../pages/ProjectsList.vue'),
      },
      {
        name: 'npmList',
        path: '/npm-list',
        component: () => import(/* webpackChunkName: "npm-list" */ '../pages/npm/NpmList.vue'),
      },
      {
        name: 'serveursCompare',
        path: '/monitoring',
        component: () => import(/* webpackChunkName: "serveur-compare" */ '../pages/monitoring/ServeursCompare.vue'),
      },
      {
        path: '/tables',
        component: () => import(/* webpackChunkName: "tables" */ '../pages/Tables.vue'),
        children: [
          { path: 'list', component: () => import(/* webpackChunkName: "tables-list" */ '../pages/tables/List.vue') },
          { path: 'chart', component: () => import(/* webpackChunkName: "tables-chart" */ '../pages/tables/Chart.vue') },
          { path: '', redirect: 'list' },
        ],
      },
      { path: '/apis-list', component: () => import(/* webpackChunkName: "apis-list" */ '../pages/ApisList.vue') },
      {
        path: '/configuration',
        component: () => import(/* webpackChunkName: "configuration" */ '../pages/Configuration.vue'),
        children: [
          {
            path: 'administration',
            component: () => import(/* webpackChunkName: "conf-administration" */ '../pages/configuration/Administration.vue'),
          },
          {
            path: 'status',
            component: () => import(/* webpackChunkName: "conf-status" */ '../pages/configuration/Status.vue'),
          },
          {
            path: 'import-export',
            component: () => import(/* webpackChunkName: "conf-import-export" */ '../pages/configuration/ImportExport.vue'),
          },
          {
            path: 'reset',
            component: () => import(/* webpackChunkName: "conf-reset" */ '../pages/configuration/Reset.vue'),
          },
          {
            path: 'monithor',
            component: () => import(/* webpackChunkName: "conf-monithor" */ '../pages/configuration/MoniThor.vue'),
          },
          { path: '', redirect: 'status' },
        ],
      },
      {
        path: '/projects/:id',
        component: () => import(/* webpackChunkName: "project-detail" */ '../pages/projects/Detail.vue'),
      },
      {
        path: '/dependencies',
        component: () => import(/* webpackChunkName: "dependencies" */ '../pages/Dependencies.vue'),
        children: [
          {
            path: 'search/:resource',
            component: () => import(/* webpackChunkName: "dep-search" */ '../pages/dependencies/Search.vue'),
          },
          { path: '', component: () => import(/* webpackChunkName: "dep-none" */ '../pages/dependencies/None.vue') },
        ],
      },
      {
        path: '/fronts-list',
        component: () => import(/* webpackChunkName: "fronts-resume" */ '../pages/fronts/FrontList.vue'),

      },
      // Always leave this last one
      { path: '*', component: () => import(/* webpackChunkName: "error" */ '../pages/Error404.vue') }, // Not found
    ],
  });

  return r;
}
