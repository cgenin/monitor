<template>
  <div class="tables-page container">
    <header-app :bc-datas="[{icon:'border_all', label:'Tables'}]"></header-app>
    <q-card>
      <q-card-main>

        <div class=" bg-light">
          <q-tabs  inverted>
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
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import HeaderApp from '../components/HeaderApp';

  export default {
    name: 'Tables',
    components: {
      HeaderApp,
    },
  };
</script>
<style lang="stylus" scoped>
  @import "../css/pages/tables.styl"
</style>
