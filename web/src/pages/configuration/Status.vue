<template>
  <div class="status-page">
    <q-list>
      <q-list-header>Status BDD</q-list-header>
        <q-item class="row sm-gutter">
            <div class="col-md-3 col-xs-6">
              <q-card class="sm-card">
                <q-card-main class="no-padding row">
                  <q-icon class="background-positive" v-if="nitrite" name="check" color="white" size="2rem"></q-icon>
                  <q-icon class="background-negative" v-if="!nitrite" name="clear" color="white" size="2rem"></q-icon>
                  <h5 class="text-center">NITRITRE</h5>
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
        <q-item-separator />
        <q-list-header>Console</q-list-header>
        <q-item class="row">
          <div class="clear-postion">
            <q-btn
              round
              color="negative"
              @click="erase"
            >
              <q-icon name="delete_forever"/>
            </q-btn>
          </div>
          <div class="console">
            <pre>@nti-monitor ~ $ Console</pre>
            <pre v-for="txt in ConsoleStore.state">{{txt.formattedDate}} - {{txt.msg}}</pre>
          </div>
        </q-item>
    </q-list>
  </div>
</template>
<script>
  import {
    QIcon,
    QBtn,
    QFixedPosition,
    QCard,
    QCardMain,
    QCardActions,
    QList,
    QListHeader,
    QItem,
    QItemSeparator,
    QTooltip
  } from 'quasar';

  import ConfigurationStore from '../../stores/ConfigurationStore'
  import MysqlStore from '../../stores/MysqlStore'
  import ConsoleStore from '../../stores/ConsoleStore'

  export default {
    name: 'ConfigurationStatus',
    components: {
      QIcon,
      QBtn,
      QFixedPosition,
      QCard,
      QCardMain,
      QCardActions,
      QList,
      QListHeader,
      QItem,
      QItemSeparator,
      QTooltip
    },
    data() {
      return {health: {}, nitrite: false, mysql: false, ConsoleStore};
    },
    methods: {
      initialize() {
        ConfigurationStore.health().then(health => {
          this.health = health;
          this.mysql = health.mysql;
          this.nitrite = health.health[0] && health.health[0].db;
        });
      },
      changeMysql() {
        MysqlStore.startOrStop()
          .then(() => {
            this.initialize();
          });
      },
      erase() {
        ConsoleStore.clear();
      }
    },
    mounted() {
      this.initialize();
    }
  }
</script>
<style lang="stylus" scoped>
  @import "configuration-status"
</style>
