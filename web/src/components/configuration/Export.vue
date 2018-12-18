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
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {migrateEvents, nameModule} from '../../store/mysql/constants';
  import {error, success} from '../../Toasts';

  const module = namespace(nameModule);

  @Component
  export default class ConfigurationExport extends Vue {
    @module.Action(migrateEvents) migrateEvents: () => Promise<any>;

    doExportJson() {
      const link = this.$refs.link;
      const l = <HTMLElement>link;
      l.click();
    }

    doExportToMysqlEvents() {
      this.migrateEvents()
        .then((result) => {
          console.log(result);
          success(`Migration effectuée avec succès pour ${result.numberOfExported}`);
        })
        .catch((err) => {
          error(err);
        });
    }
  }

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

