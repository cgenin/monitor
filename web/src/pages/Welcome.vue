<template>
  <div class="logo-container">
    <!--img src="~assets/splash.jpg" class="splash"-->
    <div class="layout-padding slide-container non-selectable no-pointer-events">

      <q-carousel arrows actions class="text-white">
        <div slot="slide" class="slide bg-primary centered">
          <span class="title">Nb Projects : {{nbProjects}}</span>
          <q-btn color="dark" glossy @click="test">Close Me</q-btn>
        </div>
        <div slot="slide" class="slide bg-secondary centered">
          <span class="title">Nb Tables : {{nbTables}}</span>
        </div>
        <q-icon slot="action" @click="goto('/projects-list')"
                name="send">
        </q-icon>
      </q-carousel>
    </div>
    <q-btn color="dark" glossy @click="test">Close Me</q-btn>

  </div>


</template>

<script>
  import {
    QCarousel, QBtn, QIcon
  } from 'quasar'
  import ProjectStore from '../stores/ProjectsStore'
  import TablesStore from '../stores/TablesStore'

  export default {
    name: 'Welcome',
    components: {
      QCarousel, QBtn, QIcon
    },
    data() {
      return {nbProjects: 0, nbTables: 0};
    },
    methods: {
      test(){
        alert('dddd')
      },
      goto(path) {
        this.$router.push(path);
      }
    },
    mounted() {
      console.log(this)
      ProjectStore
        .initialize()
        .then((list) => {
          this.nbProjects = list.length
        });
      TablesStore
        .initialize()
        .then((list) => {
          this.nbTables = list.length
        });
    },
  }
</script>

<style lang="stylus">
  .welcome-container
    display flex
    align-items center
    justify-content center
    width 100%

  .slide-container
    width 50vw
    height 50vh
    perspective 800px
    position absolute
    top 50%
    left 50%
    transform translateX(-50%) translateY(-50%)

  img.splash
    display block
    margin auto

  .slide > .title
    font-size 36px
    margin-bottom .5em

</style>
