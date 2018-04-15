<template>
  <div class="export-page">
    <div class="row sm-gutter">
      <div class="col-md-6">
        <a class="none" ref="link" href="/api/configuration/db/export.json" target="_blank">export</a>
        <q-btn @click="doExportJson" icon="file_download"  color="primary">Exporter sous format JSON
        </q-btn>
      </div>
      <div class="col-md-6">

        <q-btn @click="doExportToMysqlEvents" icon="file_download" color="positive">Archivage des
          évènements
        </q-btn>
      </div>
    </div>
  </div>
</template>
<script>
  import MysqlStore from '../../stores/MysqlStore'
  import {success, error} from '../../Toasts'

  export default {
    name: 'ConfigurationExport',
    methods: {
      doExportJson() {
        this.$refs.link.click();
      },
      doExportToMysqlEvents() {
        MysqlStore.migrateEvents()
          .then(result => {
            console.log(result);
            success(`Migration effectuée avec succès pour ${result.numberOfExported}`);
          })
          .catch(err => error(err));
      }
    }
  }
</script>
<style scoped>
  .export-page {
    margin-top: 2em;
  }

  .none {
    display: none;
  }

  .fullwidthbtn {
    width: 100%;
  }
</style>

