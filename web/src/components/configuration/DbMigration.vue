<template>
  <q-table
    class="db-migration-table"
    :data="events"
    :columns="columns"
    row-key="installedrank"
  ></q-table>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { namespace, infoSchema, events } from '../../store/mysql/constants';

  const mysqlStore = createNamespacedHelpers(namespace);

  export default {
    name: 'DbMigration',
    data() {
      return {
        columns: [
          {
            name: 'installedrank', label: '#', field: 'installedrank', align: 'left', sortable: true,
          },
          {
            name: 'strState', label: 'State', field: 'strState', align: 'left', sortable: true,
          },
          {
            name: 'desc',
            required: true,
            label: 'Description',
            align: 'left',
            field: 'description',
            sortable: true,
          },
          {
            name: 'executiontime', align: 'left', label: 'Execution time (ms)', field: 'executiontime', sortable: true,
          },
          {
            name: 'dt', align: 'left', label: 'Date', field: 'dt', sortable: true,
          },
          {
            name: 'script', align: 'left', label: 'Script\'s name', field: 'script', sortable: true,
          },
        ],
      };
    },
    computed: {
      ...mysqlStore.mapGetters([events]),
    },
    methods: {
      ...mysqlStore.mapActions([infoSchema]),
    },
    mounted() {
      this.infoSchema();
    },
  };
</script>
<style scoped>
  .db-migration-table {
    box-shadow: none;
  }
</style>
