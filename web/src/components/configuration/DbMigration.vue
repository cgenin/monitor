<template>
  <q-table
    class="db-migration-table"
    :data="events"
    :columns="columns"
    row-key="installedrank"
  ></q-table>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import { namespace } from 'vuex-class';

  import { events, infoSchema, nameModule } from '../../store/mysql/constants';

  const mysqlStore = namespace(nameModule);

  @Component
  export default class DbMigration extends Vue {
    columns = [
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
    ];

    @mysqlStore.Getter(events) events;
    @mysqlStore.Action(infoSchema) infoSchema: any;

    mounted() {
      this.infoSchema();
    }
  }
</script>
<style scoped>
  .db-migration-table {
    box-shadow: none;
  }
</style>
