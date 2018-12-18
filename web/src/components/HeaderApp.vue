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
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import BreadCrumb from './BreadCrumb';
  import HelpButton from './HelpButton';
  import {Prop} from "vue-property-decorator";

  @Component({
    components: {
      BreadCrumb, HelpButton,
    },
  })
  export default class HeaderApp extends Vue {
    @Prop(String) help:string;
    @Prop() bcDatas: any[];
  }

</script>
