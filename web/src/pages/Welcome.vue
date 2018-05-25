<template>
  <div class="welcome-container logo-container">

    <div class="panel-information column items-center justify-around">
      <div class="column justify-between items-stretch" style="width: 500px;">
          <q-btn outline color="white" label="Projets" size="lg" icon="view_list" @click="$router.push('/projects-list')">
            <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbProjects}}</q-chip>
          </q-btn>
          <q-btn outline color="white" label="Tables" size="lg" icon="border_all" @click="$router.push('/tables/list')">
            <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbTables}}</q-chip>
          </q-btn>
          <q-btn outline color="white" label="Apis" size="lg" icon="explore" @click="$router.push('/apis-list')">
            <q-chip color="secondary" style="font-size: 20px; margin: 0 15px;">{{nbApis}}</q-chip>
          </q-btn>
      </div>
    </div>
  </div>
</template>

<script>
  import ProjectStore from '../stores/ProjectsStore'
  import TablesStore from '../stores/TablesStore'
  import EndpointsStore from '../stores/EndpointsStore'
  import CardChart from '../components/CardChart'
  import QItem from "quasar-framework/src/components/list/QItem";

  export default {
    name: 'Welcome',
    components: {
      QItem,
      CardChart
    },
    data() {
      return {
        nbProjects: 0,
        nbTables: 0,
        nbApis: 0,
        datacollection: {}
      };
    },
    methods: {
      async fillData() {
        this.datacollection = {
          projects: this.nbProjects,
          tables: this.nbTables,
          apis: this.nbApis
        }
      }
    },
    computed: {
      label() {
        return `Projets ${this.nbProjects}, Tables : ${this.nbTables}, Apis: ${this.nbApis}`;
      }
    },
    mounted() {
      let promiseProject = ProjectStore
        .initialize()
        .then((list) => {
          this.nbProjects = list.length
        });
      let promiseTables = TablesStore
        .initialize()
        .then((list) => {
          this.nbTables = list.length
        });
      let promiseApis = EndpointsStore.find({})
        .then((list) => {
          this.nbApis = list.length
        });

      Promise.all([promiseProject, promiseTables, promiseApis]).then(this.fillData);
    },
  }
</script>

<style lang="stylus" scoped>
  @import "../css/pages/welcome.styl"
</style>
