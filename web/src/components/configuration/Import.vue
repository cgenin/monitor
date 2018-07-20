<template>
  <div class="import-page">
    <div class="q-if row no-wrap items-center relative-position q-input text-primary">
      <div class="q-if-inner col row no-wrap items-center relative-position">
        <label class="label-file q-btn row inline flex-center q-focusable q-hoverable relative-position q-btn-rectangle q-btn-standard q-btn-flat" for="file">
          Choisir un fichier
        </label>
        <input id="file" @change="fileSelected" class="col q-input-target input-file text-left" ref="file" type="file">
      </div>
    </div>
    <q-btn color="primary" icon="file_upload" @click="doImport">Importer</q-btn>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { importDb, namespace } from '../../store/server/constants';
  import { success, error } from '../../Toasts';

  const server = createNamespacedHelpers(namespace);

  export default {
    name: 'ConfigurationImport',
    data() {
      return {
        jsonImport: null,
      };
    },
    methods: {
      fileSelected() {
        const reader = new FileReader();
        const file = this.$refs.file.files[0];
        reader.onload = (res) => {
          this.jsonImport = res.target.result;
        };
        reader.readAsText(file);
      },
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
      },
      ...server.mapActions([importDb]),
    },
  };
</script>
<style scoped>
  .import-page {
    margin-top: 2em;
    width: 100%;
  }
</style>
