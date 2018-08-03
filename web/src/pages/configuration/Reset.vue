<template>
  <div class="reset-page">
    <q-list>
      <q-list-header>Mysql</q-list-header>
      <q-item>
        <div>
          <div class="column items-start">
            <p>Création des tables et réinitialisation</p>
            <q-btn icon="create" color="secondary" @click="doCreateSchemaMysql">Créer</q-btn>
          </div>
        </div>
      </q-item>
      <q-item-separator/>
      <q-list-header>Suppression des données calculées de nitrite</q-list-header>
      <q-item class="column items-start">
        <p>Cette option permet de supprimer les données calculées sur la base nitrite.</p>
        <q-btn icon="delete_forever" color="secondary" @click="doClearDatas">Données Calculer</q-btn>
      </q-item>
      <q-item-separator/>
      <q-list-header>Réimportation des données</q-list-header>
      <q-item class="column items-start">
        <q-alert
          color="error"
          icon="warning"
          type="warning"
          appear>
          Attention ! Lors d'une opération de rejoue des évènements, l'applicatif sera indisponible. <br/>L'action de
          fin n'est
          visible uniquement dans les logs du serveurs.
        </q-alert>
        <q-btn icon="import_export" class="reset-btn" big no-caps color="red" @click="doReset">
          Réimporter l'ensemble des données
        </q-btn>
      </q-item>
    </q-list>
    <q-modal v-model="showResultOfClearing" class="reset-modal" :content-css="{minWidth: '50vw', minHeight: '80vh'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <h5 class="title">Collection(s) deleted</h5>
        </q-toolbar>
        <q-toolbar slot="footer" class="modal-footer" right inverted>
          <q-btn
            color="primary"
            @click="closeModal"
            label="Close"
          ></q-btn>
        </q-toolbar>
        <div class="modal-body">
          <ul class="list">
            <li v-for="coll in deletedCollections" :key="coll.collectionName">
              <strong>{{coll.collectionName}}</strong>
              :{{coll.size}}
            </li>
          </ul>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { namespace as namespaceMysql, createSchema } from '../../store/mysql/constants';
  import { namespace as namespaceServer, remove, clearDatas, deletedCollections } from '../../store/server/constants';
  import { success, error } from '../../Toasts';

  const mysqlStore = createNamespacedHelpers(namespaceMysql);
  const serverStore = createNamespacedHelpers(namespaceServer);

  export default {
    name: 'ConfigurationReset',
    data() {
      return {
        showResultOfClearing: false,
      };
    },
    computed: {
      ...serverStore.mapGetters([deletedCollections]),
    },
    methods: {
      doCreateSchemaMysql() {
        this.createSchema()
          .then((res) => {
            if (res.creation) {
              success(`Nombre de migrations effectuées : ${res.report.nbMigration}`);
            } else {
              success('Impossible d\'exécuter la requête');
            }
          })
          .catch((err) => {
            error(err);
          });
      },
      doReset() {
        this.remove()
          .then(() => {
            success();
          })
          .catch((err) => {
            error(err);
          });
      },
      doClearDatas() {
        this.clearDatas()
          .then(() => {
            this.showResultOfClearing = true;
          })
          .catch((err) => {
            error(err);
          });
      },
      closeModal() {
        this.showResultOfClearing = false;
      },
      ...mysqlStore.mapActions([createSchema]),
      ...serverStore.mapActions([clearDatas, remove]),
    },
  };
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/reset.styl"
</style>
