<template>
  <div class="tables-page">
    <bread-crumb :datas="[{icon:'border_all', label:'Tables'}]"/>
    <div class="tabs-container container bg-light">
      <q-tabs color="light">
        <q-route-tab icon="fa-list" label="Liste des tables"
                     to="/tables/list" color="primary"
                     slot="title">
        </q-route-tab>
        <q-route-tab icon="fa-line-chart" label="Graphique"
                     to="/tables/chart" color="primary"
                     slot="title">
        </q-route-tab>
      </q-tabs>
    </div>
    <router-view/>
  </div>
</template>
<script>
  import {
    QTabs, QRouteTab, QCard, QCardMain
  } from 'quasar';
  import BreadCrumb from '../components/BreadCrumb'

  export default {
    name: 'Tables',
    components: {
      QTabs, QRouteTab, QCard, QCardMain, BreadCrumb
    }
  }
</script>
<style lang="stylus">
  @import "../themes/quasar.variables.styl"
  .tables-page
    .tabs-container
      margin: 0 30px
      padding: 5px
      border: 1px solid $border
    .q-tabs-normal
      .q-tabs-bar
        color: $primary
</style>
