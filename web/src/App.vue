<template>
  <!-- Don't drop "q-app" class -->
  <div id="q-app">
    <q-layout
      view="lHh Lpr lFf"
      :left-class="{'bg-primary': true}"
    >
      <q-layout-header>
        <q-toolbar>
          <q-btn
            flat
            @click="menuExpand()"
            class="burger-icon"
            :icon="opened ? 'close' : 'menu'"
          >
          </q-btn>

          <q-toolbar-title>
            Anti-monitor
            <div slot="subtitle">Récapitualtif des projets Micro services</div>
          </q-toolbar-title>
          <!--<q-btn flat class="grey-9" @click="$router.push('/configuration/status')">-->
          <!--<q-icon name="build" />-->
          <!--</q-btn>-->
        </q-toolbar>
      </q-layout-header>
      <q-layout-drawer
        v-model="opened"
        :content-class="$q.theme === 'mat' ? 'bg-grey-2' : null"
      >
        <q-list no-border link inset-delimiter>
          <div class="sidebar-header">
            <q-list-header class="side-header">
              <img src="./assets/sinestro_corps_logo_small.png" alt="logo anti-monitor">
              <span>Anti-monitor</span>
            </q-list-header>
            <q-list-header>
              <h3>Menu</h3>
            </q-list-header>
          </div>
          <q-item to="/" exact>
            <q-item-side icon="home"/>
            <q-item-main label="Welcome" sublabel="Page de résumé"/>
          </q-item>
          <q-collapsible icon="fa-cogs" label="Micro services" opened>
            <q-item to="/projects-list">
              <q-item-side icon="view_list"/>
              <q-item-main label="Liste des projets" sublabel="Résumé des derniers build"/>
            </q-item>
            <q-item to="/tables">
              <q-item-side icon="border_all"/>
              <q-item-main label="Liste des tables" sublabel="liaisons entre les services et les tables"/>
            </q-item>
            <q-item to="/apis-list">
              <q-item-side icon="explore"/>
              <q-item-main label="Liste des apis" sublabel="Liste des traitements"/>
            </q-item>
            <q-item to="/dependencies">
              <q-item-side icon="link"/>
              <q-item-main label="Dépendances" sublabel="Dépendance entre les Micro Services"/>
            </q-item>
          </q-collapsible>
          <q-item to="/configuration">
            <q-item-side icon="build"/>
            <q-item-main label="Console d'administration" sublabel="Configuration et outils"/>
          </q-item>
        </q-list>
      </q-layout-drawer>
      <q-page-container>
        <router-view/>
      </q-page-container>
    </q-layout>
  </div>
</template>

<script>

  /*
   * Root component
   */
  export default {
    name: 'App',
    data() {
      return {
        opened: this.$q.platform.is.desktop
      }
    },
    methods: {
      menuExpand() {
        if (!this.opened) {
          this.opened = true
        }
        else {
          this.opened = false
        }
      }
    }
  }
</script>

<style lang="stylus" scoped>
</style>
