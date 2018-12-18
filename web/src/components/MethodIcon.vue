<template>
  <q-chip :icon="icon" :color="color">
    {{method}}
  </q-chip>
</template>
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import {Prop} from "vue-property-decorator";

  @Component
  export default class MethodIcon extends Vue {
   @Prop(String) method: string;

    get icon() {
      if (this.method) {
        switch (this.method) {
          case 'POST':
            return 'move_to_inbox';
          case 'GET':
            return 'unarchive';
          case 'DELETE':
            return 'delete_forever';
          case 'PUT':
            return 'add_box';
          case 'HEAD':
            return 'face';
          default:
            console.error(`method unkonwn${this.method}`);
        }
      }
      return '';
    }

    get color() {
      switch (this.method) {
        case 'POST':
          return 'purple';
        case 'GET':
          return 'blue';
        case 'DELETE':
          return 'red';
        case 'PUT':
          return 'green';
        case 'HEAD':
          return 'orange';
        default:
          return '';
      }
    }
  }
</script>
