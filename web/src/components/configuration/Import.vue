<template>
  <div class="import-page">
    <div class="q-if row no-wrap items-center relative-position q-input text-primary">
      <div class="q-if-inner col row no-wrap items-center relative-position">
        <label
          class="label-file q-btn row inline flex-center q-focusable q-hoverable relative-position q-btn-rectangle q-btn-standard q-btn-flat"
          for="file">
          Choisir un fichier
        </label>
        <input id="file" @change="fileSelected" class="col q-input-target input-file text-left" ref="file" type="file">
      </div>
    </div>
    <q-btn color="primary" icon="file_upload" @click="doImport">Importer</q-btn>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {importDb, nameModule} from '../../store/server/constants';
  import {error, success} from '../../Toasts';

  const server = namespace(nameModule);
  @Component
  export default class ConfigurationImport extends Vue {
    jsonImport: string = null;
    @server.Action(importDb) importDb: (i: string) => Promise<null>;

    fileSelected() {
      const reader = new FileReader();
      const file: any = (<any>this.$refs.file).files[0];
      reader.onload = (res) => {
        this.jsonImport = (<any>res.target).result;
      };
      reader.readAsText(file);
    }

    doImport() {
      if (this.jsonImport) {
        this.importDb(this.jsonImport)
          .then(() => {
            this.jsonImport = null;
            success();
          })
          .catch((err) => {
            error(err);
          });
      }
    }
  }
</script>
<style scoped>
  .import-page {
    margin-top: 2em;
    width: 100%;
  }
</style>
