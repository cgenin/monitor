<template>
  <div class="logo-container">
    <!--<img src="~assets/splash.jpg" class="splash">-->
    <div class="layout-padding slide-container">
      <q-card class="container">
        <q-card-title>
          <h3>Nombre de projets, tables, apis</h3>
        </q-card-title>
        <q-card-separator/>
        <q-card-main>
        <!--<q-carousel arrows autoplay infinite dots class="text-white">-->
          <!--<div slot="slide" class="slide bg-primary centered">-->
            <!--<span class="title">Nb Projects : {{nbProjects}}-->
            <!--</span>-->
          <!--</div>-->
          <!--<div slot="slide" class="slide bg-secondary centered">-->
            <!--<span class="title">Nb Tables : {{nbTables}}</span>-->
          <!--</div>-->
        <!--</q-carousel>-->
          <card-chart
            card-title="Nombre de Projets, Tables, Apis"
            :data="datacollection"
          ></card-chart>
        </q-card-main>
      </q-card>
    </div>
  </div>
</template>

<script>
  import {
    QCarousel,
    QBtn,
    QIcon,
    QCard,
    QCardTitle,
    QCardMain,
    QCardSeparator
  } from 'quasar'
  import ProjectStore from '../stores/ProjectsStore'
  import TablesStore from '../stores/TablesStore'
  import CardChart from '../components/CardChart'

  export default {
    name: 'Welcome',
    components: {
      QCarousel,
      QBtn,
      QIcon,
      QCard,
      QCardTitle,
      CardChart,
      QCardMain,
      QCardSeparator
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

      Promise.all([promiseProject, promiseTables]).then(this.fillData);
    },
  }
</script>

<style lang="stylus">
  .welcome-container
    display flex
    align-items center
    justify-content center
    width 100%

  img.splash
    display block
    margin auto

  .slide > .title
    font-size 36px
    margin-bottom .5em

  .pie-container
    display flex
    justify-content center
    > div
      width 40%

</style>
