<template>
  <div>
    <q-btn @click="modal= true" color="tertiary" v-if="content" small>
      <q-icon name="change_history"/>
    </q-btn>
    <q-modal ref="layoutModal" v-model="modal" :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            Change Log
          </div>
          <q-btn flat @click="modal= false">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div class="layout-padding">
          <vue-markdown :source="content" :show="true"></vue-markdown>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'ChangelogButton',
    data() {
      return {modal: false};
    },
    props: ['content', 'id'],
    components: {VueMarkdown},
    mounted() {
      console.log(this.content);
    }
  }
</script>
