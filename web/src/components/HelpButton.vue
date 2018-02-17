<template>
  <div class="help-modal-btn">
    <q-btn v-if="text" @click="modal=true" icon="help" round/>
    <q-modal ref="lModal" v-model="modal" :content-css="{minWidth: '90vw', minHeight: '90vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            Aide
          </div>
          <q-btn flat @click="$refs.lModal.close()">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div>
        <vue-markdown>{{text}}</vue-markdown>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import {QBtn, QIcon, QToolbar, QModal, QModalLayout} from 'quasar'
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'HelpButton',
    props: ['text'],
    components: {
      QBtn, QIcon, QToolbar, QModal, QModalLayout, VueMarkdown
    },
    data() {
      return {modal: false};
    }
  }
</script>
