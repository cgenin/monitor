<template>
  <markdown-button :content="content" color="tertiary" title="Change Log" icon="change_history"></markdown-button>
</template>
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import MarkdownButton from './MarkdownButton';
  import {Prop} from "vue-property-decorator";

  @Component({components: {MarkdownButton},})
  export default class ChangelogButton extends Vue {
    modal = false;
    @Prop(String) content: string;
  };
</script>
