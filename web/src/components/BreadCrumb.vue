<template>
  <ul class="breadcrumb noprint">
    <li>
      <router-link to="/">
        <q-icon name="home"/>
      </router-link>
    </li>
    <li v-for="d in datas">
      <router-link :to="d.path || ''" active-class="router-link-active">
        <q-icon :name="d.icon"/>
        {{d.label}}
      </router-link>
    </li>
  </ul>
</template>
<script>
  import {
    QIcon
  } from 'quasar';

  export default {
    name: 'BreadCrumb',
    props: ['datas'],
    components: {QIcon},
  }
</script>
