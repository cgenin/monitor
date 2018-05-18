<template>
  <!-- Don't drop "q-app" class -->
  <div id="q-app">
    <q-layout
      view="lHh Lpr lFf"
      :left-class="{'bg-primary': false}"
    >
      <q-layout-header>
        <q-toolbar color="header" text-color="header">
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
        </q-toolbar>
      </q-layout-header>
      <q-layout-drawer

        v-model="opened"
      >
        <div class="tool-bar">
          <q-list no-border link inset-delimiter>
            <div class="tool-bar-header">
              <q-list-header class="main">
                <img src="./assets/sinestro_corps_logo_small.png" alt="logo anti-monitor">
                <h3>&nbsp;Anti-monitor</h3>
              </q-list-header>
              <q-list-header>
                <h4>Menu</h4>
              </q-list-header>
            </div>
            <q-item to="/" exact>
              <q-item-side icon="home"/>
              <q-item-main label="Accueil"/>
            </q-item>
            <q-collapsible icon="fa-cogs" label="Micro services">
              <q-item to="/projects-list">
                <q-item-side icon="view_list"/>
                <q-item-main label="Liste des projets" sublabel="Résumé des derniers build"/>
              </q-item>
              <q-item to="/tables">
                <q-item-side icon="border_all"/>
                <q-item-main label="Liste des tables" sublabel="liaisons services / tables"/>
              </q-item>
              <q-item to="/apis-list">
                <q-item-side icon="explore"/>
                <q-item-main label="Liste des apis" sublabel="Liste des traitements"/>
              </q-item>
              <q-item to="/dependencies">
                <q-item-side icon="link"/>
                <q-item-main label="Dépendances" sublabel="Dép entre MicroServices"/>
              </q-item>
            </q-collapsible>
            <q-item to="/npm-list" v-if="moniThorUrl">
              <q-item-side icon="view_list"/>
              <q-item-main label="NPM" sublabel="Informations sur les projets NPM"/>
            </q-item>
            <q-item to="/monitoring" v-if="moniThorUrl">
              <q-item-side icon="graphic_eq"/>
              <q-item-main label="Monitoring" sublabel="Informations serveurs"/>
            </q-item>
            <q-item to="/configuration">
              <q-item-side icon="build"/>
              <q-item-main label="Console d'administration" sublabel="Configuration et outils"/>
            </q-item>
          </q-list>
        </div>
      </q-layout-drawer>
      <q-page-container>
        <router-view/>
      </q-page-container>
    </q-layout>
  </div>
</template>

<script>

  import NpmStore from './stores/NpmStore';
  import ConfigurationStore from './stores/ConfigurationStore';

  /*
   * Root component
   */
  export default {
    name: 'App',
    data() {
      return {
        opened: this.$q.platform.is.desktop,
        npms: null,
        moniThorUrl: null
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
    },
    mounted() {
      NpmStore.initialize().then((npms) => this.npms = npms);
      ConfigurationStore.initialize()
        .then(() => {
          this.moniThorUrl = ConfigurationStore.moniThorUrl;
        });
    }
  }
</script>
