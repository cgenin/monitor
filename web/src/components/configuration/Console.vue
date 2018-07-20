<template>
  <q-item class="row">
  <div class="clear-position">
      <q-btn
        round
        color="negative"
        @click="erase"
      >
        <q-icon name="delete_forever"/>
      </q-btn>
    </div>
    <div class="console">
      <pre>@nti-monitor ~ $ Console</pre>
      <pre v-for="txt in messages" :key="txt.key">{{txt.formattedDate}} - {{txt.msg}}</pre>
    </div>
  </q-item>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';

  const { mapActions, mapGetters } = createNamespacedHelpers('console');

  export default {
    name: 'Console',
    methods: {
      ...mapActions(['initialize', 'erase']),
    },
    computed: {
      ...mapGetters(['messages']),
    },
    mounted() {
      this.initialize();
    },
  };
</script>
<style lang="stylus" scoped>
  // Console
  .clear-position
    position absolute
    top 15px
    right 23px

  // Console block
  .console
    color #C0C0C0
    font-weight bold
    width 100%
    min-height 300px
    margin 0
    padding 15px
    background-color #343434
    border 1px solid #CABFA6
    border-radius 5px
    pre
      position relative
      font-size 14px
      padding-left 30px
      padding-bottom 5px
      display block
      overflow hidden
      margin 0
      &:nth-child(1)
        margin-top 25px
      &.breakline
        padding-bottom 12px

</style>
