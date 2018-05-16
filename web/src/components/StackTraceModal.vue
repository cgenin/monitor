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
<script>
  import VueMarkdown from 'vue-markdown'

  export default {
    name: 'StacktraceModal',
    props: ['message', 'stacktrace', 'opened', 'callback'],
    components: {VueMarkdown},
    computed: {
      showStackTrace() {
        return this.stacktrace !== null
          && this.stacktrace !== undefined
          && this.stacktrace !== '';
      },
      isOpen() {
        return this.opened;
      }
    },
    methods: {
      escape() {
        this.callback();
      }
    }
  }
</script>
<style scoped>
  h4 {
    margin-top: .1em;
    margin-bottom: .5em;
  }
</style>
