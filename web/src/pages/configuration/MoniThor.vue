<template>
  <div class="moni-thor">
    <q-list>
      <q-list-header>Configuration Moni-Thor</q-list-header>
      <q-item class="column items-start">
        <q-input type="text" float-label="Url du serveur" v-model="moniThorUrl"
                 class="field-input"></q-input>
      </q-item>
      <q-item class="column items-stretch">
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
  </div>
</template>
<script>
  import ConfigurationStore from '../../stores/ConfigurationStore'
  import {error, success} from '../../Toasts'

  export default {
    name: 'MoniThor',
    data() {
      return {
        moniThorUrl: null
      }
    },
    methods: {
      refresh() {
        ConfigurationStore.initialize()
          .then(() => {
            this.moniThorUrl = ConfigurationStore.moniThorUrl || '';
          });
      },
      save() {
        const configuration = Object.assign({}, ConfigurationStore._state, {moniThorUrl: this.moniThorUrl});
        ConfigurationStore.save(configuration)
          .then(() => success())
          .catch((err) => error(err));
      }

    },
    mounted() {
      this.refresh();
    }
  }
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/monithor.styl"
</style>

