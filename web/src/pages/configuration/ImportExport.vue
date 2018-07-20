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
<script>
  import Import from '../../components/configuration/Import';
  import Export from '../../components/configuration/Export';

  export default {
    name: 'ConfigurationImportExport',
    components: { Import, Export },
  };

</script>
