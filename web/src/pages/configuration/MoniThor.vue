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
  import { createNamespacedHelpers } from 'vuex';
  import { nameModule, global, initialize, save as saveConfiguration } from '../../store/configuration/constants';
  import { error, success } from '../../Toasts';

  const conf = createNamespacedHelpers(nameModule);
  export default {
    name: 'MoniThor',
    data() {
      return {
        moniThorUrl: null,
      };
    },
    computed: {
      ...conf.mapGetters([global]),
    },
    methods: {
      refresh() {
        this.initialize().then(() => {
          this.moniThorUrl = this.global.moniThorUrl || '';
        });
      },
      save() {
        const configuration = Object.assign({}, this.global, { moniThorUrl: this.moniThorUrl });
        this.saveConfiguration(configuration)
          .then(() => success())
          .catch(err => error(err));
      },
      ...conf.mapActions({ initialize, saveConfiguration }),
    },
    mounted() {
      this.refresh();
    },
  };
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/monithor.styl"
</style>

