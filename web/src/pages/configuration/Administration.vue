<template>
  <div class="administration-page">
    <q-list>
      <q-list-header>Configuration Mysql&nbsp;-&nbsp;
        <q-toggle v-model="activerMysql" @input="changeMysql" color="teal-8" label="Afficher"></q-toggle>
      </q-list-header>
      <q-item class="column items-start">
        <div v-if="activerMysql">
          <q-input class="mysql-field" v-model="mysql.host" float-label="Host"></q-input>
          <q-input class="mysql-field" v-model="mysql.port" type="number" float-label="Port"></q-input>
          <q-input class="mysql-field" v-model="mysql.user" float-label="Utilisateur"></q-input>
          <q-input class="mysql-field" v-model="mysql.password" type="password" float-label="Mot de passe"></q-input>
          <q-input class="mysql-field" v-model="mysql.database" float-label="Base de données"></q-input>
          <div class="row mysql-buttons align-center">
            <div>
              <q-toggle v-model="mysql.activate" color="teal-10" label="Activer"></q-toggle>
            </div>
            <div>
              <q-btn @click="test" color="blue-grey-5">Tester la connexion</q-btn>
            </div>
          </div>
        </div>
      </q-item>
      <q-item-separator/>
      <q-list-header>Filtres sur les librairies Java.
        <span class="text-grey-6" style="font-size:.95em;">
          (Ce filtre permet de spécifier uniquement les librairies interne à afficher dans la vue "projet".)
        </span>
      </q-list-header>
      <q-item class="column items-stretch">

        <div>
          <q-chips-input color="green" v-model="javaFilters" placeholder="Ajouter un nouveau filtre"></q-chips-input>
        </div>
      </q-item>
      <q-item-separator/>
      <q-list-header>Filtres sur les dépendences Npm</q-list-header>
      <q-item class="column items-stretch">
        <div>
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
      </q-item>
    </q-list>
    <q-inner-loading :visible="testConnectionLoading">
      <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
    </q-inner-loading>

    <stack-trace-modal :opened="errorModal" :callback="hideModal" :stacktrace="modal.stacktrace"
                       :message="modal.message"></stack-trace-modal>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'Vuex';
  import { initialize, namespace as namespaceConf, save as saveConfiguration, global as state } from '../../store/configuration/constants';
  import { namespace as namespaceMysql, testConnectionLoading, test as testMysqlConnection } from '../../store/mysql/constants';
  import StackTraceModal from '../../components/StackTraceModal';
  import { success, error } from '../../Toasts';

  const conf = createNamespacedHelpers(namespaceConf);
  const mysqlStore = createNamespacedHelpers(namespaceMysql);

  export default {
    name: 'ConfigurationAdministration',
    components: { StackTraceModal },
    data() {
      return {
        activerMysql: false,
        mysql: {},
        javaFilters: [],
        npmFilters: [],
        errorModal: false,
        modal: {
          message: '',
        },
      };
    },
    methods: {
      hideModal() {
        this.errorModal = false;
      },
      changeMysql(newVal) {
        if (newVal) {
          this.mysql = {
            host: 'localhost',
            port: 3306,
            database: 'antimonitor',
            activate: false,
          };
        } else {
          this.mysql = {};
        }
      },
      refresh() {
        const { javaFilters, npmFilters, mysql } = this.state;
        this.javaFilters = javaFilters;
        this.npmFilters = npmFilters;
        this.mysql = Object.assign({}, mysql);
        this.activerMysql = !!(mysql.host && mysql.port && mysql.user && mysql.password && mysql.database);
      },
      save() {
        const { javaFilters, npmFilters, activerMysql } = this;
        const mysql = (activerMysql) ? this.mysql : {};
        const configuration = Object.assign({}, this.state, { javaFilters, npmFilters, mysql });
        this.saveConfiguration(configuration)
          .then(() => success())
          .catch(err => error(err));
      },
      test() {
        const { mysql } = this;
        this.testMysqlConnection(mysql)
          .then((json) => {
            if (json.state === 'success') {
              success('Les paramètres de connexion à la base de données sont corrects.');
              return;
            }
            const { msgError, stacktrace } = json;
            this.modal = {
              message: msgError,
              stacktrace,
            };
            this.errorModal = true;
          })
          .catch((err) => {
            console.log(err);
            this.modal = {
              message: 'Une erreur est survenue...',
            };
            this.errorModal = true;
          });
      },
      ...conf.mapActions({ initialize, saveConfiguration }),
      ...mysqlStore.mapActions({ testMysqlConnection }),
    },
    computed: {
      ...conf.mapGetters({ state }),
      ...mysqlStore.mapGetters([testConnectionLoading]),
    },
    mounted() {
      this.initialize()
        .then(() => {
          this.refresh();
        })
        .catch(err => console.log(err));
    },
  };
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/administration.styl"
</style>
