import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

function load(component) {
  // '@' is aliased to src/components
  return () => import(`./pages/${component}.vue`)
}

export default new VueRouter({
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
  scrollBehavior: () => ({y: 0}),

  routes: [
    {name:'welcome', path: '/', component: load('Welcome')},
    {name:'projectsList',path: '/projects-list', component: load('ProjectsList')},
    {
      path: '/tables',
      component: load('Tables'),
      children: [
        {path: 'list', component: load('tables/List')},
        {path: 'chart', component: load('tables/Chart')},
        {path: '', redirect: 'list'},
      ]
    },
    {path: '/apis-list', component: load('ApisList')},
    {
      path: '/configuration',
      component: load('Configuration'),
      children: [
        {path: 'administration', component: load('configuration/Administration')},
        {path: 'status', component: load('configuration/Status')},
        {path: 'import-export', component: load('configuration/ImportExport')},
        //{path: 'export', component: load('configuration/Export')},
        {path: 'reset', component: load('configuration/Reset')},
        {path: '', redirect: 'status'},
      ]
    },
    {path: '/projects/:id', component: load('projects/Detail')},
    {
      path: '/dependencies',
      component: load('Dependencies'),
      children: [
        {path: 'search/:resource', component: load('dependencies/Search')},
        {path: '', component: load('dependencies/None')},
      ]
    },
    // Always leave this last one
    {path: '*', component: load('Error404')} // Not found
  ]
})
