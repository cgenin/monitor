<template>
  <q-breadcrumbs>
    <q-icon name="arrow_forward" slot="separator" slot-scope="props" />
    <q-breadcrumbs-el icon="home" to="/">
    </q-breadcrumbs-el>
    <q-breadcrumbs-el v-for="d in datas"
                      :label="d.label"
                      :icon="d.icon"
                      active-color="secondary"
                      color="light"
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
