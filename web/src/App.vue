<template>
  <!-- Don't drop "q-app" class -->
  <div id="q-app">
    <q-layout
      ref="layout"
      view="lHh Lpr fff"
      :left-class="{'bg-grey-2': true}"
    >
      <q-toolbar slot="header" class="glossy">
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
          <q-list-header>Menu</q-list-header>
          <q-item to="/" exact>
            <q-item-side icon="home"/>
            <q-item-main label="Welcome" sublabel="Page de résumé"/>
          </q-item>
          <q-item to="/projects-list">
            <q-item-side icon="view_list"/>
            <q-item-main label="Liste des projets" sublabel="Résumé des derniers build"/>
          </q-item>
          <q-item to="/tables-list">
            <q-item-side icon="border_all"/>
            <q-item-main label="Liste des tables" sublabel="liaisons entre les services et les tables"/>
          </q-item>
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
      QItemSide,
      QItemMain
    },
  }
</script>

<style></style>
