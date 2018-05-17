<template>
    <q-btn @click="openModal" :color="computedColor" v-if="content" small>
      <q-icon :name="icon"/>
      <q-modal ref="layoutModal" v-model="modal" :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
        <q-modal-layout>
          <q-toolbar slot="header">
            <div class="q-toolbar-title">
              {{title}}
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
    </q-btn>

</template>
<script>
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'MarkdownButton',
    data() {
      return {modal: false};
    },
    computed: {
      computedColor() {
        return this.color || 'primary';
      }
    },
    methods: {
      openModal() {
        this.modal = true;
        this.$emit('click');
      }
    },
    props: ['content', 'id', 'title', 'icon', 'color', 'onClick'],
    components: {VueMarkdown},
    mounted() {
    }
  }
</script>
