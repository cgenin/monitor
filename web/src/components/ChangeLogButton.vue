<template>
  <div>
    <q-btn @click="modal= true" color="secondary" v-if="content">
      <q-icon name="change_history"></q-icon>
    </q-btn>
    <q-modal v-model="modal" :content-css="{minWidth: '85vw', minHeight: '85vh', padding:'1em'}">
      <vue-markdown>{{content}}</vue-markdown>
    </q-modal>
  </div>
</template>
<script>
  import {QBtn, QIcon, QModal, QModalLayout} from 'quasar';
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'ChangelogButton',
    data() {
      return {modal: false};
    },
    props: ['content'],
    components: {QBtn, QIcon, QModal, QModalLayout, VueMarkdown}
  }
</script>
