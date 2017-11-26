<template>
  <div class="import-page">
    <div class="inputs">
      <input @change="fileSelected" class="q-input-target" ref="file" type="file">
    </div>
    <hr>
    <q-btn class="btn-fullwidth" color="primary" icon="file_upload" @click="doImport">Importer</q-btn>
  </div>
</template>
<script>
  import {QBtn} from 'quasar';
  import {success, error} from '../../Toasts';
  import ConfigurationStore from '../../stores/ConfigurationStore'

  export default {
    name: 'ConfigurationImport',
    components: {QBtn},
    data() {
      return {
        jsonImport: null
      }
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
          ConfigurationStore.importDb(this.jsonImport)
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
  }
</script>
<style scoped>
  .import-page {
    margin-top: 2em;
  }

  .inputs {
    margin-bottom: 1em;
  }

  .btn-fullwidth {
    width: 100%
  }
</style>
