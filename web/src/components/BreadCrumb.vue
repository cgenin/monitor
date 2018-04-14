<template>
  <q-breadcrumbs class="root-breadcrumb">
    <q-icon color="primary" name="fa-chevron-right" class="fa-1x" slot="separator" slot-scope="props"/>
    <q-breadcrumbs-el icon="home" class="root" to="/" exact/>
    <q-breadcrumbs-el v-for="d in datas"
                      :label="d.label"
                      :icon="d.icon"
                      class="child"
                      :key="`${d.path}-${d.label}`"
                      :to="d.path || ''"
                      active-class="router-link-active">
    </q-breadcrumbs-el>
  </q-breadcrumbs>
</template>
<script>
  export default {
    name: 'BreadCrumb',
    props: ['datas'],
  }
</script>
<style lang="stylus">
  @import '~variables'

  $breadcrumb-color = white !important
  $breadcrumb-padding = 3px

  .root-breadcrumb
    .q-breadcrumbs-separator
      padding: 6px 3px 3px
      .fa.fa-chevron-right.fa-1x
        font-size: 1em
        font-weight: 700
    .q-breadcrumbs-el
      color $breadcrumb-color
      background-color $primary
      padding $breadcrumb-padding
      text-align center
      &.root
        margin-right 8px
        .q-icon
          margin-right 0
    .q-breadcrumbs-last
      color $breadcrumb-color
      .child
        background-color $secondary
        padding $breadcrumb-padding
        font-weight 700


</style>
