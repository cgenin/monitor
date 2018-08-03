<template>
  <div class="welcome-container logo-container">

    <div class="panel-information column items-center justify-around">
      <div class="column justify-between items-stretch" style="width: 500px;">
        <q-btn outline color="white" label="Services" size="lg" icon="fas fa-cogs"
               @click="$router.push('/projects-list')">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbProjects}}</q-chip>
        </q-btn>
        <q-btn outline color="white" label="Tables" size="lg" icon="border_all" @click="$router.push('/tables/list')">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbTables}}</q-chip>
        </q-btn>
        <q-btn outline color="white" label="Apis" size="lg" icon="explore" @click="$router.push('/apis-list')">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbApis}}</q-chip>
        </q-btn>
        <q-btn outline color="white" label="Web-apps" size="lg" icon="fab fa-chrome"
               @click="$router.push('/fronts-list')">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbFronts}}</q-chip>
        </q-btn>
      </div>
    </div>
  </div>
</template>

<script>
  import { createNamespacedHelpers } from 'vuex';
  import {
    namespace as namespaceMicroService,
    loadTables,
    nbTables,
    nbApis,
    loadApis, loadProjects, nbProjects,
  } from '../store/microservices/constants';
  import { namespace as namespaceFronts, nbFronts, loadResume } from '../store/fronts/constants';

  const microServicesStore = createNamespacedHelpers(namespaceMicroService);
  const frontsStore = createNamespacedHelpers(namespaceFronts);

  export default {
    name: 'Welcome',
    methods: {
      ...microServicesStore.mapActions([loadTables, loadApis, loadProjects]),
      ...frontsStore.mapActions([loadResume]),

    },
    computed: {
      ...microServicesStore.mapGetters([nbTables, nbApis, nbProjects]),
      ...frontsStore.mapGetters([nbFronts]),
    },
    mounted() {
      const promiseProject = this.loadProjects();
      const promiseTables = this.loadTables();
      const promiseApis = this.loadApis({});
      const promiseFronts = this.loadResume();
      Promise.all([promiseProject, promiseTables, promiseApis, promiseFronts])
        .then(() => console.log('all loaded'));
    },
  };
</script>

<style lang="stylus" scoped>
  @import "../css/pages/welcome.styl"
</style>
