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
            <q-item :to="Welcome" exact>
              <q-item-side icon="home"/>
              <q-item-main label="Accueil"/>
            </q-item>
            <q-collapsible icon="fas fa-cogs" label="Micro services" v-model="openedMs">
              <q-item :to="ProjectsList">
                <q-item-side icon="view_list"/>
                <q-item-main label="Liste des projets" sublabel="Résumé des derniers build"/>
              </q-item>
              <q-item :to="Tables">
                <q-item-side icon="border_all"/>
                <q-item-main label="Liste des tables" sublabel="liaisons services / tables"/>
              </q-item>
              <q-item :to="ApisList">
                <q-item-side icon="explore"/>
                <q-item-main label="Liste des apis" sublabel="Liste des traitements"/>
              </q-item>
              <q-item :to="Dependencies">
                <q-item-side icon="link"/>
                <q-item-main label="Dépendances" sublabel="Dép entre MicroServices"/>
              </q-item>
            </q-collapsible>
            <q-collapsible icon="fab fa-chrome" label="Web apps" v-model="openedFront">
              <q-item :to="FrontList">
                <q-item-side icon="list_alt"/>
                <q-item-main label="Liste des applications" sublabel="Résumé des derniers build"/>
              </q-item>
              <q-item :to="FrontDependencies">
                <q-item-side icon="link"/>
                <q-item-main label="Dépendances" sublabel="Dép entre un MicroService et applications"/>
              </q-item>
            </q-collapsible>
            <q-item to="/rt/npm-list" v-if="moniThorUrl">
              <q-item-side icon="featured_play_list"/>
              <q-item-main label="NPM" sublabel="Informations sur les projets NPM"/>
            </q-item>
            <q-item to="/rt/monitoring" v-if="moniThorUrl">
              <q-item-side icon="graphic_eq"/>
              <q-item-main label="Monitoring" sublabel="Informations serveurs"/>
            </q-item>
            <q-item to="/rt/configuration">
              <q-item-side icon="build"/>
              <q-item-main label="Console d'administration" sublabel="Configuration et outils"/>
            </q-item>
            <q-item @click.native="showAbout">
              <q-item-side icon="copyright"/>
              <q-item-main label="A propos"/>
            </q-item>
          </q-list>
        </div>
      </q-layout-drawer>
      <q-page-container>
        <transition :name="transitionName">
          <router-view/>
        </transition>
        <q-modal v-model="openAboutModal" minimized>
          <div style="padding: 50px">
            <div class="q-display-1 q-mb-md">Version : {{packageJson.version}}</div>
            <p>Tu comprends, je suis mon meilleur modèle car entre penser et dire, il y a un monde de différence et
              c'est très, très beau d'avoir son propre moi-même ! Ça respire le meuble de Provence, hein ?</p>
              <p>Tu comprends, je ne suis pas un simple danseur car là, j'ai un chien en ce moment à côté de moi et je
                le caresse, parce que spirituellement, on est tous ensemble, ok ? Et là, vraiment, j'essaie de tout
                coeur de donner la plus belle réponse de la terre !</p>
            <q-btn color="red" v-close-overlay label="Close"    @click="openAboutModal = false"/>
          </div>
        </q-modal>
      </q-page-container>
    </q-layout>

  </div>
</template>

<script>
  import packageJson from '../package.json';
  import { createNamespacedHelpers } from 'vuex';
  import {
    ApisList,
    Dependencies,
    FrontList,
    isFront,
    isMicroService,
    ProjectsList,
    Tables,
    Welcome,
    FrontDependencies,
  } from './Routes';
  import { initialize, moniThorUrl, nameModule as namespaceConf } from './store/configuration/constants';
  import { loadNpmList, nameModule as namespaceMonithor } from './store/moniThor/constants';

  let first = true;
  const confStore = createNamespacedHelpers(namespaceConf);
  const monithorStore = createNamespacedHelpers(namespaceMonithor);
  /*
   * Root component
   */
  export default {
    name: 'App',
    data() {
      return {
        openedMs: false,
        openedFront: false,
        transitionName: 'slide-right',
        opened: this.$q.platform.is.desktop,
        Welcome,
        ProjectsList,
        ApisList,
        Dependencies,
        Tables,
        FrontList,
        FrontDependencies,
        openAboutModal: false,
        packageJson,
      };
    },
    computed: {
      ...confStore.mapGetters([moniThorUrl]),
    },
    methods: {
      menuExpand() {
        this.opened = !this.opened;
      },
      showAbout() {
        this.openAboutModal = true;
      },
      ...confStore.mapActions([initialize]),
      ...monithorStore.mapActions([loadNpmList]),
    },
    watch: {
      $route(to, from) {
        const toDepth = to.path.split('/').length;
        const fromDepth = from.path.split('/').length;
        this.transitionName = toDepth < fromDepth ? 'slide-right' : 'slide-left';
        if (first) {
          this.openedMs = isMicroService(to);
          this.openedFront = isFront(to);
          first = false;
        }
      },
    },
    mounted() {
      this.initialize()
        .then(() => this.loadNpmList());
    },
  };
</script>
