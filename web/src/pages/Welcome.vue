<template>
  <div class="welcome-container logo-container">

    <div class="panel-information column items-stretch text-white">
      <div class="row col">
        <div class="row justify-center items-center  col">
          <h2>
            <q-icon name="view_list"></q-icon>
          </h2>
          <q-item to="/projects-list">
            <h2 class="link">
               Projets : {{nbProjects}}
            </h2>
          </q-item>
        </div>
        <div class="row justify-center items-center col">
          <h2>
            <q-icon name="border_all" ></q-icon>
          </h2>
          <q-item to="/tables/list">
            <h2 class="link">
               Tables : {{nbTables}}
            </h2>
          </q-item>
        </div>
      </div>
      <div class="col row justify-center items-center ">
        <h2>
          <q-icon name="explore"/>
        </h2>
        <q-item to="/apis-list">
          <h2 class="link">
            &nbsp;Apis : {{nbApis}}
          </h2>
        </q-item>
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
  .welcome-container
    display flex
    align-items center
    justify-content center
    width 100%

  .pie-container
    display flex
    justify-content center
    > div
      width 40%

  .panel-information
    min-height 75vh
    min-width 75vw
    background-color: rgba(6, 6, 6, 0.67);
    h2.link
      text-decoration underline
      cursor pointer
      text-align center


</style>
