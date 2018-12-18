<template>
  <q-breadcrumbs class="root-breadcrumb" separator="">
    <q-breadcrumbs-el icon="home" class="root" to="/rt" exact/>
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
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {Prop} from "vue-property-decorator";


  @Component
  export default class BreadCrumb extends Vue {
    @Prop() datas: any[];
  }
</script>
<style lang="stylus">
</style>
