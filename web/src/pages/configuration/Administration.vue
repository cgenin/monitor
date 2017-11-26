<template>
  <div class="administration-page">
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
  import {QChipsInput, QBtn} from 'quasar';
  import {success, error} from '../../Toasts'
  import ConfigurationStore from '../../stores/ConfigurationStore';

  export default {
    name: 'ConfigurationAdministration',
    components: {QChipsInput, QBtn},
    data() {
      return {
        javaFilters: [],
        npmFilters: []
      }
    },
    methods: {
      refresh() {
        const {javaFilters, npmFilters} = ConfigurationStore;
        this.javaFilters = javaFilters;
        this.npmFilters = npmFilters
      },
      save() {
        const {javaFilters, npmFilters} = this;
        const configuration = Object.assign({}, ConfigurationStore.state, {javaFilters, npmFilters});
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
