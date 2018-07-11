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
<script>
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'HelpButton',
    props: ['text'],
    components: {VueMarkdown},
    data() {
      return {modal: false};
    }
  }
</script>
