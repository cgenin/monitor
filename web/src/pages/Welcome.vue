<template>
  <div class="welcome-container logo-container">
    <div class="panel-information column items-center justify-around">
      <div class="column justify-between items-stretch" style="width: 500px;">
        <q-btn outline color="white" label="Services" size="lg" icon="fas fa-cogs"
               @click="$router.push(ProjectsList)">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbProjects}}</q-chip>
        </q-btn>
        <q-btn outline color="white" label="Tables" size="lg" icon="border_all" @click="$router.push(Tables)">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbTables}}</q-chip>
        </q-btn>
        <q-btn outline color="white" label="Apis" size="lg" icon="explore" @click="$router.push(ApisList)">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbApis}}</q-chip>
        </q-btn>
        <q-btn outline color="white" label="Web-apps" size="lg" icon="fab fa-chrome"
               @click="$router.push(FrontList)">
          <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbFronts}}</q-chip>
        </q-btn>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {
    loadApis,
    loadProjects,
    loadTables,
    nameModule as namespaceMicroService,
    nbApis,
    nbProjects,
    nbTables,
  } from '../store/microservices/constants';
  import {ApisList, FrontList, ProjectsList, Tables} from '../Routes';
  import {loadResume, nameModule as namespaceFronts, nbFronts} from '../store/fronts/constants';

  const microServicesStore = namespace(namespaceMicroService);
  const frontsStore = namespace(namespaceFronts);

  @Component
  export default class WelcomePage extends Vue {
    ProjectsList = ProjectsList;
    FrontList = FrontList;
    Tables = Tables;
    ApisList = ApisList;
    @microServicesStore.Action(loadTables) loadTables: () => Promise<number>;
    @microServicesStore.Action(loadApis) loadApis: (param: any) => Promise<number>;
    @microServicesStore.Action(loadProjects) loadProjects: () => Promise<number>;
    @frontsStore.Action(loadResume) loadResume: () => Promise<number>;

    @microServicesStore.Getter(nbTables) nbTables: number;
    @microServicesStore.Getter(nbApis) nbApis: number;
    @microServicesStore.Getter(nbProjects) nbProjects: number;
    @frontsStore.Getter(nbFronts) nbFronts: number;

    mounted() {
      const promiseProject = this.loadProjects();
      const promiseTables = this.loadTables();
      const promiseApis = this.loadApis({});
      const promiseFronts = this.loadResume();
      Promise.all([promiseProject, promiseTables, promiseApis, promiseFronts])
        .then(() => console.log('all loaded'));
    }
  };
</script>

<style lang="stylus" scoped>
  @import "../css/pages/welcome.styl"
</style>
