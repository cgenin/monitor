<template>
  <div class="import-export-page">
    <q-list>
      <q-list-header>Import</q-list-header>
      <q-item>
        <import></import>
      </q-item>
      <q-list-header>Export</q-list-header>
      <q-item>
        <export></export>
      </q-item>
    </q-list>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import Import from '../../components/configuration/Import';
  import Export from '../../components/configuration/Export';

  @Component({components: {Import, Export},})
  export default class ConfigurationImportExport extends Vue {
  };

</script>
