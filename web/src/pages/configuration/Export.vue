<template>
  <div class="export-page">
    <a class="none" ref="link" href="/api/configuration/db/export.json" target="_blank">export</a>
    <q-btn @click="doExportJson" icon="file_download" class="fullwidthbtn" color="primary">Exporter sous format JSON
    </q-btn>
  </div>
</template>
<script>

  export default {
    name: 'ConfigurationExport',
    methods: {
      doExportJson() {
        this.$refs.link.click();
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

