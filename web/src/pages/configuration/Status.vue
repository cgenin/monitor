<template>
  <div class="status-page">
    <q-list>
      <q-list-header>Status BDD</q-list-header>
      <q-item class="row sm-gutter">
        <div class="col-md-3 col-xs-6">
          <q-card class="sm-card row">
            <q-card-main class="no-padding row">
              <q-icon class="background-positive" v-if="nitrite" name="check" color="white" size="2rem"></q-icon>
              <q-icon class="background-negative" v-if="!nitrite" name="clear" color="white" size="2rem"></q-icon>
              <h5 class="text-center">
                NITRITRE
              </h5>
            </q-card-main>
          </q-card>
        </div>
        <div class="col-md-3 col-xs-6">
          <q-card class="sm-card row">
            <q-card-main class="no-padding row">
              <q-icon class="background-positive" v-if="mysql" name="check" color="white" size="2rem"></q-icon>
              <q-icon class="background-negative" v-if="!mysql" name="clear" color="white" size="2rem"></q-icon>
              <h5 class="text-center">
                MYSQL
              </h5>
            </q-card-main>
            <q-card-actions vertical>
              <q-btn v-if="mysql" @click="changeMysql" flat>
                <q-icon name="stop"></q-icon>
                <q-tooltip>Désactiver</q-tooltip>
              </q-btn>
              <q-btn v-if="!mysql" @click="changeMysql" flat>
                <q-icon name="play_arrow"></q-icon>
                <q-tooltip>Activer</q-tooltip>
              </q-btn>
            </q-card-actions>
          </q-card>
        </div>
      </q-item>
      <div v-if="mysql">
        <q-item-separator/>
        <q-collapsible>
          <template slot="header">
            <q-chip color="primary" small class="q-mr-sm">
              Db migration.
            </q-chip>
            <q-item-main label=""/>
            <q-item-side right>
              if the DB must be updated or not.
            </q-item-side>
          </template>
          <div>
            <db-migration></db-migration>
          </div>
        </q-collapsible>
      </div>
      <q-item-separator/>
      <q-collapsible>
        <template slot="header">
          <q-chip color="primary" small class="q-mr-sm">
            Console
          </q-chip>
          <q-item-main label=""/>
          <q-item-side right>
            Get the server's logs.
          </q-item-side>
        </template>
        <div>
          <console></console>
        </div>
      </q-collapsible>
    </q-list>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { namespace as namespaceConf, mysql } from '../../store/configuration/constants';
  import { namespace as namespaceMysql, startOrStop } from '../../store/mysql/constants';
  import { namespace as namespaceServer, getHealth, health, nitrite } from '../../store/server/constants';
  import DbMigration from '../../components/configuration/DbMigration';
  import Console from '../../components/configuration/Console';

  const confStore = createNamespacedHelpers(namespaceConf);
  const server = createNamespacedHelpers(namespaceServer);
  const mysqlStore = createNamespacedHelpers(namespaceMysql);

  export default {
    name: 'ConfigurationStatus',
    components: { DbMigration, Console },
    methods: {
      changeMysql() {
        this.startOrStop()
          .then(() => {
            // this.initialize();
          });
      },
      ...server.mapActions([getHealth]),
      ...mysqlStore.mapActions([startOrStop]),
    },
    computed: {
      ...confStore.mapGetters([mysql]),
      ...server.mapGetters([health, nitrite]),
    },
    mounted() {
      this.getHealth();
    },
  };
</script>
