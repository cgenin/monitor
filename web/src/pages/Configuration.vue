<template>
  <div class="configuration-page">
    <q-card>
      <q-card-title>
        <h4>Console d'adminisration</h4>
      </q-card-title>
      <q-card-separator></q-card-separator>
      <q-card-main>
        <q-tabs inverted>
          <q-route-tab icon="radio" label="Traitement"
                       to="/configuration"
                       exact
                       slot="title"></q-route-tab>
          <q-route-tab icon="cloud_upload" label="Import"
                       to="/configuration/import"
                       exact
                       slot="title"></q-route-tab>
          <q-route-tab icon="send" label="Export"
                       to="/configuration/export"
                       exact
                       slot="title"></q-route-tab>
          <q-route-tab icon="history" label="Reset"
                       to="/configuration/reset"
                       exact
                       slot="title"></q-route-tab>
        </q-tabs>
        <router-view></router-view>
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
    QSpinnerDots
  } from 'quasar';

  export default {
    name: 'Configuration',
    components: {
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
      QSpinnerDots
    }
  }
</script>
