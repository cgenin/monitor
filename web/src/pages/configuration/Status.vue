<template>
  <div class="status-page">
    <div class="row">
      <div class="col-md-4 col-xs-12">
        <h4>NITRITRE :
          <q-icon v-if="nitrite" name="check_circle" color="positive"></q-icon>
          <q-icon v-if="!nitrite" name="highlight_off" color="negative"></q-icon>
        </h4>
      </div>
      <div class="col-md-4 col-xs-12">
        <h4>MYSQL :
          <q-icon v-if="mysql" name="check_circle" color="positive"></q-icon>
          <q-icon v-if="!mysql" name="highlight_off" color="negative"></q-icon>
        </h4>
      </div>
      <div class="col-md-4 col-xs-12 button-mysql">
        <q-btn v-if="mysql" @click="changeMysql" outline icon="stop">Désactiver</q-btn>
        <q-btn v-if="!mysql" @click="changeMysql" outline icon="play_arrow">Activer</q-btn>
      </div>
    </div>
    <hr>
    <div class="row">
      <div class="col-xs-12">
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
        </div>

      </div>
    </div>
  </div>
</template>
<script>
  import {QIcon, QBtn, QFixedPosition} from 'quasar';

  import ConfigurationStore from '../../stores/ConfigurationStore'
  import ConsoleStore from '../../stores/ConsoleStore'

  export default {
    name: 'ConfigurationStatus',
    components: {
      QIcon, QBtn, QFixedPosition
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
        ConfigurationStore.startOrStopMysql()
          .then(() => {
            this.initialize();
          });
      },
      erase() {
      }
    },
    mounted() {
      this.initialize();
    }
  }
</script>
<style scoped>
  .button-mysql {
    padding-top: 1.14rem;
  }

  .clear-postion {
    display: flex;
    justify-content: flex-end;
    padding-right: 15px;
    margin-bottom: -60px;
  }

  .console {
    color: #C0C0C0;
    font-weight: bold;
    width: 100%;
    min-height: 300px;
    margin: 0;
    background-color: #343434;
    border: 1px solid #CABFA6;
    border-radius: 5px;
  }

  .console pre {
    position: relative;
    font-size: 14px;
    padding-left: 30px;
    padding-bottom: 0;
    display: block;
    overflow: hidden;
    margin: 0;
  }

  .console pre:nth-child(1) {
    margin-top: 25px;
  }

  .console pre.breakline {
    padding-bottom: 12px;

  }

</style>
