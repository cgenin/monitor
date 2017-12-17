<template>
  <div class="administration-page">
    <div>
      <q-toggle v-model="activerMysql" @change="changeMysql" color="teal-8" label="Activer Mysql"></q-toggle>
    </div>
    <div v-if="activerMysql">
      <q-input v-model="mysql.host" float-label="Host"></q-input>
      <q-input v-model="mysql.port" type="number" float-label="Port"></q-input>
      <q-input v-model="mysql.user" float-label="Utilisateur"></q-input>
      <q-input v-model="mysql.password" type="password" float-label="Mot de passe"></q-input>
      <q-input v-model="mysql.database" float-label="Base de données"></q-input>
    </div>
    <hr>
    <div>
      <p class="caption">Filtres sur les librairies Java : </p>
      <q-chips-input color="green" v-model="javaFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
    </div>
    <hr>
    <div>
      <p class="caption">Filtres sur les dépendences Npm :</p>
      <q-chips-input color="blue-grey" v-model="npmFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
    </div>
    <hr>
    <div class="buttons">
      <div class="button">
        <q-btn flat color="black" @click="refresh" icon="refresh">Rafraîchir</q-btn>
      </div>
      <div class="button">
        <q-btn color="primary" @click="save" icon="save">Sauvegarder</q-btn>
      </div>
    </div>
  </div>
</template>
<script>
  import {QChipsInput, QBtn, QToggle, QInput} from 'quasar';
  import {success, error} from '../../Toasts'
  import ConfigurationStore from '../../stores/ConfigurationStore';

  export default {
    name: 'ConfigurationAdministration',
    components: {QChipsInput, QBtn, QToggle, QInput},
    data() {
      return {
        activerMysql: false,
        mysql: {},
        javaFilters: [],
        npmFilters: []
      }
    },
    methods: {
      changeMysql() {
        if (this.activerMysql) {
          this.mysql = {host: 'localhost', port: 3306, database: 'antimonitor'};
        }
        else {
          this.mysql = {};
        }
      },
      refresh() {
        const {javaFilters, npmFilters, mysql} = ConfigurationStore;
        this.javaFilters = javaFilters;
        this.npmFilters = npmFilters;
        this.mysql = mysql;
        this.activerMysql = !!(mysql.host && mysql.port && mysql.user && mysql.password && mysql.database);
      },
      save() {
        const {javaFilters, npmFilters, activerMysql} = this;
        const mysql = (activerMysql) ? this.mysql : {};
        const configuration = Object.assign({}, ConfigurationStore.state, {javaFilters, npmFilters, mysql});
        ConfigurationStore.save(configuration)
          .then(() => success())
          .catch((err) => error(err));
      }
    },
    mounted() {
      ConfigurationStore.initialize()
        .then(() => {
          this.refresh();
        })
        .catch((err) => console.log(err))
    }
  }
</script>
<style scoped>
  .administration-page {
    margin-top: 2em;
  }

  .administration-page .buttons {
    margin-top: 2em;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .administration-page .buttons .button {
    margin-left: 1em;
    margin-right: 1em;
  }
</style>
