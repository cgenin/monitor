<template>
  <div class="configuration-page">
    <header-app :bc-datas="[{icon:'build', label:'Console d\'administration'}]" />
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
          <q-route-tab icon="cloud_upload" label="Import / Export"
                       to="/configuration/import-export"
                       exact
                       slot="title"/>
          <q-route-tab icon="history" label="Reset"
                       to="/configuration/reset"
                       exact
                       slot="title"/>
          <q-route-tab icon="graphic_eq" label="Moni-thor"
                       to="/configuration/monithor"
                       exact
                       slot="title"/>
        </q-tabs>
        <router-view/>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import HeaderApp from '../components/HeaderApp';

  export default {
    name: 'Configuration',
    components: {
      HeaderApp,
    }
  }
</script>

<style lang="stylus" scoped>
  @import "configuration/configuration"
</style>
