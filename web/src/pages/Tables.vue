<template>
  <div class="tables-page container">
    <header-app :bc-datas="[{icon:'border_all', label:'Tables'}]"></header-app>
    <div class="tabs-container bg-light">
      <q-tabs color="light">
        <q-route-tab icon="view_list" label="Liste des tables"
                     to="/tables/list" color="primary"
                     slot="title">
        </q-route-tab>
        <q-route-tab icon="show_chart" label="Graphique"
                     to="/tables/chart" color="primary"
                     slot="title">
        </q-route-tab>
      </q-tabs>
    </div>
    <router-view/>
  </div>
</template>
<script>
  import HeaderApp from '../components/HeaderApp';

  export default {
    name: 'Tables',
    components: {
      HeaderApp
    }
  }
</script>
<style lang="stylus">
  @import '~variables'
  .tables-page
    .tabs-container
      border-left: 1px solid $border
      border-top: 1px solid $border
      border-right: 1px solid $border
    .q-tabs-normal
      .q-tabs-head:not(.scrollable)
        padding 0
      .q-tabs-bar
        color: $primary
</style>
