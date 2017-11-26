<template>
  <div class="reset-page">
    <p class="caption text-red-7">
      Attention ! Lors d'une opération de rejoue des évènements l'applicatif sera indisponible. L'action de fin n'est visible uniquement dans les logs du serveurs.
    </p>
    <q-btn icon="import_export" class="reset-btn" big no-caps color="red" @click="doReset">
      Réimporter l'ensemble des données
    </q-btn>
  </div>
</template>
<script>
  import {QBtn, Toast} from 'quasar';
  import AppsStore from '../../stores/AppsStore';

  export default {
    name: 'ConfigurationReset',
    components: {QBtn},
    methods: {
      doReset() {
        AppsStore.remove()
          .then(() => {
            console.log('OK');
            Toast.create['positive']({
              html: `<strong>Mise à jour effectuée avec succés. :)</strong>`,
              timeout: 2500
            });
          })
          .catch((err) => {
            console.log(err);
            Toast.create['negative']({
              html: `<strong>Erreur Technique</strong>`,
              timeout: 2500
            });
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
</style>
