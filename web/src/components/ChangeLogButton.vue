<template>
  <div>
    <q-btn @click="modal= true" color="secondary" v-if="content" small>
      <q-icon name="change_history"></q-icon>
    </q-btn>
    <q-modal ref="layoutModal" v-model="modal" :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            Change Log
          </div>
          <q-btn flat @click="$refs.layoutModal.close()">
            <q-icon name="close" />
          </q-btn>
        </q-toolbar>
        <vue-markdown>{{content}}</vue-markdown>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import {QBtn, QIcon, QToolbar, QModal, QModalLayout} from 'quasar';
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'ChangelogButton',
    data() {
      return {modal: false};
    },
    props: ['content'],
    components: {QBtn, QIcon, QToolbar, QModal, QModalLayout, VueMarkdown}
  }
</script>
