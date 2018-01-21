<template>
  <div class="configuration-page">
    <bread-crumb :datas="[{icon:'build', label:'Console d\'administration'}]"></bread-crumb>
    <q-card class="container">
      <q-card-title>
        <h3>Console d'adminisration</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <q-tabs inverted>
          <q-route-tab icon="screen_share" label="Status"
                       to="/configuration/status"
                       exact
                       slot="title"/>
          <q-route-tab icon="radio" label="Traitement"
                       to="/configuration/administration"
                       exact
                       slot="title"/>
          <q-route-tab icon="cloud_upload" label="Import"
                       to="/configuration/import"
                       exact
                       slot="title"/>
          <q-route-tab icon="send" label="Export"
                       to="/configuration/export"
                       exact
                       slot="title"/>
          <q-route-tab icon="history" label="Reset"
                       to="/configuration/reset"
                       exact
                       slot="title"/>
        </q-tabs>
        <router-view/>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import {
    QCard,
    QCardTitle,
    QCardSeparator,
    QCardMain,
    QInput,
    QBtn,
    QSelect,
    QTabs,
    QRouteTab,
    QList,
    QItem,
    QItemMain,
    QRadio,
    QInfiniteScroll,
    QSpinnerDots,
    QIcon
  } from 'quasar';
  import BreadCrumb from '../components/BreadCrumb'

  export default {
    name: 'Configuration',
    components: {
      BreadCrumb,
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInput,
      QBtn,
      QSelect,
      QTabs,
      QRouteTab,
      QList,
      QItem,
      QItemMain,
      QRadio,
      QInfiniteScroll,
      QSpinnerDots,
      QIcon
    }
  }
</script>

<style lang="stylus" scoped>
  @import "configuration/configuration"
</style>
