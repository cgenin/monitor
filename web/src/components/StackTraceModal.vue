<template>
  <q-modal v-model="isOpen" @escape-key="escape" @hide="escape">
    <div class="layout-padding">
      <h4 class="text-negative">
        <q-icon name="thumb_down" color="negative"></q-icon>
        - {{message}}
      </h4>
      <vue-markdown v-if="showStackTrace" :source="stacktrace" :show="true"></vue-markdown>
    </div>
  </q-modal>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {Prop} from "vue-property-decorator";
  import VueMarkdown from 'vue-markdown';

  @Component({components: {VueMarkdown},})
  export default class StacktraceModal extends Vue {
    @Prop(String) message: string;
    @Prop(String) stacktrace?: string;
    @Prop(Boolean) opened: boolean;
    @Prop() callback: () => void;

    get showStackTrace() {
      return this.stacktrace !== null
        && this.stacktrace !== undefined
        && this.stacktrace !== '';
    }

    get isOpen() {
      return this.opened;
    }

    escape() {
      this.callback();
    }
  }
</script>
<style scoped>
  h4 {
    margin-top: .1em;
    margin-bottom: .5em;
  }
</style>
