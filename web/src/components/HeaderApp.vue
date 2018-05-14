<template>
  <div class="row items-center breadcrumb-area">
    <div class="col-10">
      <bread-crumb :datas="bcDatas"/>
    </div>
    <div class="col-2 row justify-end">
      <help-button :text="help"/>
    </div>
  </div>
</template>
<script>
  import BreadCrumb from './BreadCrumb'
  import HelpButton from './HelpButton'

  export default {
    name: 'HeaderApp',
    props: ['help', 'bcDatas'],
    components: {
      BreadCrumb, HelpButton
    }
  }
</script>
