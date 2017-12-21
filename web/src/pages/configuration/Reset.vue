<template>
  <div class="reset-page">
    <div>
      <h4>Mysql</h4>
      <div class="row items-center inline">
        <div class="label">Création des tables et réinitialisation =></div>
        <q-btn icon="create" flat color="secondary" @click="doCreateSchemaMysql">Créer</q-btn>
      </div>
    </div>
    <h4>Réimportation des données</h4>
    <p class="caption text-red-7">
      Attention ! Lors d'une opération de rejoue des évènements l'applicatif sera indisponible. L'action de fin n'est
      visible uniquement dans les logs du serveurs.
    </p>
    <q-btn icon="import_export" class="reset-btn" big no-caps color="red" @click="doReset">
      Réimporter l'ensemble des données
    </q-btn>
  </div>
</template>
<script>
  import {QBtn} from 'quasar';
  import {success, error} from '../../Toasts'
  import AppsStore from '../../stores/AppsStore';
  import MysqlStore from '../../stores/MysqlStore';

  export default {
    name: 'ConfigurationReset',
    components: {QBtn},
    methods: {
      doCreateSchemaMysql() {
        MysqlStore.createSchema()
          .then((res) => {
            if (res.creation) {
              success(`<strong>${res.report}</strong>`);
            }
            else {
              success(`<strong>Aucune table créée</strong>`);
            }
          })
          .catch((err) => {
            error(err);
          });
      },
      doReset() {
        AppsStore.remove()
          .then(() => {
            success();
          })
          .catch((err) => {
            error(err);
          })
      }
    }
  }
</script>
<style scoped>
  .reset-page {
    margin-top: 2em;
  }

  .reset-page .reset-btn {
    width: 100%;
  }

  .reset-page .label {
    margin-right: 15px;
    font-weight: bold;
  }
</style>
