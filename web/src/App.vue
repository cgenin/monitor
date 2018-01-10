<template>
  <!-- Don't drop "q-app" class -->
  <div id="q-app">
    <q-layout
      ref="layout"
      view="lHh Lpr fff"
      :left-class="{'bg-primary': true}"
    >
      <q-toolbar slot="header">
        <q-btn
          flat
          @click="$refs.layout.toggleLeft()"
        >
          <q-icon name="menu"/>
        </q-btn>

        <q-toolbar-title>
          Anti-monitor
          <div slot="subtitle">Récapitualtif des projets Micro services</div>
        </q-toolbar-title>
      </q-toolbar>

      <div slot="left">
        <!--
          Use <q-side-link> component
          instead of <q-item> for
          internal vue-router navigation
        -->

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
          <q-side-link item to="/" exact>
            <q-item-side icon="home"/>
            <q-item-main label="Welcome" sublabel="Page de résumé"/>
          </q-side-link>
          <q-side-link item to="/projects-list">
            <q-item-side icon="view_list"/>
            <q-item-main label="Liste des projets" sublabel="Résumé des derniers build"/>
          </q-side-link>
          <q-side-link item to="/tables-list">
            <q-item-side icon="border_all"/>
            <q-item-main label="Liste des tables" sublabel="liaisons entre les services et les tables"/>
          </q-side-link>
          <q-item to="/apis-list">
            <q-item-side icon="explore"/>
            <q-item-main label="Liste des apis" sublabel="Liste des traitements"/>
          </q-item>
          <q-side-link item to="/configuration/status">
            <q-item-side icon="build"/>
            <q-item-main label="Console d'administration" sublabel="Configuration et outils"/>
          </q-side-link>
        </q-list>
      </div>
      <router-view/>
    </q-layout>
  </div>
</template>

<script>
  import {
    QLayout,
    QToolbar,
    QToolbarTitle,
    QBtn,
    QIcon,
    QList,
    QListHeader,
    QItem,
    QSideLink,
    QItemSide,
    QItemMain
  } from 'quasar';

  /*
   * Root component
   */
  export default {
    name: 'App',
    components: {
      QLayout,
      QToolbar,
      QToolbarTitle,
      QBtn,
      QIcon,
      QList,
      QListHeader,
      QItem,
      QSideLink,
      QItemSide,
      QItemMain
    },
  }
</script>

<style></style>
