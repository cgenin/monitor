<template>
  <q-table
    class="db-migration-table"
    :data="events"
    :columns="columns"
    row-key="installedrank"
  />
</template>
<script>
  import MysqlStore from '../../stores/MysqlStore'
  import {date} from 'quasar'

  export default {
    name: 'DbMigration',
    data() {
      return {
        events: [],
        columns: [
          {name: 'installedrank', label: '#', field: 'installedrank', align: 'left', sortable: true},
          {name: 'strState', label: 'State', field: 'strState', align: 'left', sortable: true},
          {
            name: 'desc',
            required: true,
            label: 'Description',
            align: 'left',
            field: 'description',
            sortable: true
          },
          {name: 'executiontime', align: 'left', label: 'Execution time (ms)', field: 'executiontime', sortable: true},
          {name: 'dt', align: 'left', label: 'Date', field: 'dt', sortable: true},
          {name: 'script', align: 'left', label: `Script's name`, field: 'script', sortable: true},
        ],
      }
    },
    mounted() {
      MysqlStore.infoSchema()
        .then(arr => {
          this.events = arr.map(obj => {

            const dt = (obj.installedon) ? date.formatDate(new Date(obj.installedon), 'YYYY-MM-DD HH:mm:ss') : '';
            const strState = obj.state.name;
            return {dt, strState, ...obj};
          });
        });
    }

  }
</script>
<style scoped>
  .db-migration-table {
    box-shadow: none;
  }
</style>
