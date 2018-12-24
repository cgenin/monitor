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
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {nameModule, global, initialize, save as saveConfiguration} from '../../store/configuration/constants';
  import {error, success} from '../../Toasts';
  import {ConfiguationState} from '../../store/configuration/types';

  const configuration = namespace(nameModule);

  @Component
  export default class MoniThor extends Vue {
    moniThorUrl: string = null;
    @configuration.Getter(global) global: ConfiguationState;
    @configuration.Action(initialize) initialize: () => Promise<void>;
    @configuration.Action(saveConfiguration) saveConfiguration: (conf: ConfiguationState) => Promise<void>;

    refresh() {
      this.initialize().then(() => {
        this.moniThorUrl = this.global.moniThorUrl || '';
      });
    }

    save() {
      const conf = Object.assign({}, this.global, {moniThorUrl: this.moniThorUrl});
      this.saveConfiguration(conf)
        .then(() => success())
        .catch(err => error(err));
    }

    mounted() {
      this.refresh();
    }
  };
</script>
<style lang="stylus" scoped>
  @import "../../css/pages/monithor.styl"
</style>

