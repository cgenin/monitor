<template>
  <div class="help-modal-btn">
    <q-btn v-if="text" @click="modal=true" icon="fas fa-question" color="primary" round/>
    <q-modal ref="lModal" v-model="modal"
             :content-css="{minWidth: '90vw', minHeight: '90vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            Aide
          </div>
          <q-btn flat @click="modal = false">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div class="layout-padding">
          <vue-markdown :source="text" :show="true"></vue-markdown>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import VueMarkdown from 'vue-markdown';
  import { Prop } from "vue-property-decorator";

  @Component({
    components: {
      VueMarkdown,
    },
  })
  export default class HelpButton extends Vue {
    modal: boolean = false;
    @Prop(String) text:string;
  }
</script>
