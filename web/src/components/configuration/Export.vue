<template>
  <div class="export-page">
    <div class="row sm-gutter">
      <div class="col-md-6 row justify-center">
        <a class="none" ref="link" href="/api/configuration/db/export.json" target="_blank">export</a>
        <q-btn @click="doExportJson" icon="file_download" color="primary">Exporter sous format JSON
        </q-btn>
      </div>
      <div class="col-md-6 row justify-center">

        <q-btn @click="doExportToMysqlEvents" icon="file_download" color="positive">Archivage des
          évènements
        </q-btn>
      </div>
    </div>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { namespace, migrateEvents } from '../../store/mysql/constants';
  import { success, error } from '../../Toasts';

  const mysqlStore = createNamespacedHelpers(namespace);

  export default {
    name: 'ConfigurationExport',
    methods: {
      doExportJson() {
        this.$refs.link.click();
      },
      doExportToMysqlEvents() {
        this.migrateEvents()
          .then((result) => {
            console.log(result);
            success(`Migration effectuée avec succès pour ${result.numberOfExported}`);
          })
          .catch(err => error(err));
      },
      ...mysqlStore.mapActions([migrateEvents]),
    },
  };
</script>
<style scoped>
  .export-page {
    margin-top: 2em;
    width: 100%;
  }

  .none {
    display: none;
  }

</style>

