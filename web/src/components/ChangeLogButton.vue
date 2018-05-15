<template>
  <markdown-button :content="content" color="tertiary" title="Change Log" icon="change_history"></markdown-button>
</template>
<script>
  import MarkdownButton from './MarkdownButton'

  export default {
    name: 'ChangelogButton',
    data() {
      return {modal: false};
    },
    props: ['content', 'id'],
    components: {MarkdownButton},
    mounted() {
    }
  }
</script>
