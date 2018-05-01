<template>
  <div class="header-app row items-center" style="margin:1.5em">
    <div class="col-11">
      <bread-crumb :datas="bcDatas"/>
    </div>
    <div class="col-1" v-if="showHelpButton">
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
    },
    computed: {
      showHelpButton() {
        return (this.help);
      }
    }

  }
</script>
