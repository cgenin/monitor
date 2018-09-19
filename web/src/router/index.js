import Vue from 'vue';
import VueRouter from 'vue-router';
import 'moment/locale/fr';
import 'moment/locale/en-gb';
import WelcomePage from '../pages/Welcome';
import { ApisList, CONTEXT_ROUTE, Dependencies, FrontList, ms, ProjectsList, Tables, Welcome, FrontDependencies } from '../Routes';

Vue.use(VueRouter);


const NoneDependencies = () => import(/* webpackChunkName: "dep-none" */ '../pages/dependencies/None.vue');
export default new VueRouter({
  /*
   * NOTE! VueRouter "history" mode DOESN'T works for Cordova builds,

   * build publicPath back to '' so Cordova builds work again.
   */

  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),


  routes: [
    // Default
    { name: 'index', path: '/', redirect: Welcome },
    { name: 'indexRt', path: CONTEXT_ROUTE, redirect: Welcome },
    // Welcome
    {
      path: Welcome,
      component: WelcomePage,
    },
    // Micro services
    {
      path: ProjectsList,
      component: () => import(/* webpackChunkName: "project-list" */ '../pages/ProjectsList.vue'),
    },
    {
      path: Tables,
      component: () => import(/* webpackChunkName: "tables" */ '../pages/Tables.vue'),
      children: [
        { path: 'list', component: () => import(/* webpackChunkName: "tables-list" */ '../pages/tables/List.vue') },
        { path: 'chart', component: () => import(/* webpackChunkName: "tables-list" */ '../pages/tables/Chart.vue') },

        { path: '', redirect: 'list' },
      ],
    },
    { path: ApisList, component: () => import(/* webpackChunkName: "apis-list" */ '../pages/ApisList.vue') },

    {
      path: ms('/projects/:id'),
      component: () => import(/* webpackChunkName: "project-detail" */ '../pages/projects/Detail.vue'),
    },
    {
      path: Dependencies,
      component: () => import(/* webpackChunkName: "dependencies" */ '../pages/Dependencies.vue'),
      children: [
        {
          path: 'search/:resource',
          component: () => import(/* webpackChunkName: "dep-search" */ '../pages/dependencies/Search.vue'),
        },
        {
          path: 'search/:resource',
          component: () => import(/* webpackChunkName: "dep-search" */ '../pages/dependencies/Search.vue'),
        },
        { path: '', component: NoneDependencies },
      ],
    },
    // Configuration
    {
      path: '/rt/configuration',
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
    // Front
    {
      path: FrontList,
      component: () => import(/* webpackChunkName: "fronts-resume" */ '../pages/fronts/FrontList.vue'),

    },
    {
      path: FrontDependencies,
      component: () => import(/* webpackChunkName: "dependencies" */ '../pages/fronts/FrontDependencies.vue'),
      children: [
        { path: '', component: NoneDependencies },
      ],
    },
    // Moni-thor
    {
      name: 'npmList',
      path: '/rt/npm-list',
      component: () => import(/* webpackChunkName: "npm-list" */ '../pages/npm/NpmList.vue'),
    },
    {
      name: 'serveursCompare',
      path: '/rt/monitoring',
      component: () => import(/* webpackChunkName: "serveur-compare" */ '../pages/monitoring/ServeursCompare.vue'),
    },

    // Always leave this last one
    { path: '*', component: () => import(/* webpackChunkName: "error" */ '../pages/Error404.vue') }, // Not found
  ],
});
