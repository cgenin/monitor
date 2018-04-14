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
      <q-item-separator />
      <q-list-header>Réimportation des données</q-list-header>
      <q-item class="column items-start">
        <q-alert
          color="error"
          icon="warning"
          appear>
          Attention ! Lors d'une opération de rejoue des évènements, l'applicatif sera indisponible. <br />L'action de fin n'est
          visible uniquement dans les logs du serveurs.
        </q-alert>
        <q-btn icon="import_export" class="reset-btn" big no-caps color="red" @click="doReset">
          Réimporter l'ensemble des données
        </q-btn>
      </q-item>
    </q-list>
  </div>
</template>
<script>
  import {success, error} from '../../Toasts'
  import AppsStore from '../../stores/AppsStore';
  import MysqlStore from '../../stores/MysqlStore';

  export default {
    name: 'ConfigurationReset',
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
<style lang="stylus" scoped>
  .reset-page
    margin-top 2em
    .reset-btn
      margin-top 15px

</style>
