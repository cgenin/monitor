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
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import VueMarkdown from 'vue-markdown';
  import {Prop} from "vue-property-decorator";

  @Component({
    components: {
      VueMarkdown
    },
  })
  export default class MarkdownButton extends Vue {
    modal:boolean = false;
    @Prop(String) content:string;
    @Prop(String) title:string;
    @Prop(String) icon:string;
    @Prop(String) color?: string;

    get computedColor() {
      return this.color || 'primary';
    }

    openModal() {
      this.modal = true;
      this.$emit('click');
    }
  };
</script>
