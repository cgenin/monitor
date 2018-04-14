<template>
  <q-chip :icon="icon" :color="color">
    {{method}}
  </q-chip>
</template>
<script>

  export default {
    name: 'MethodIcon',
    props: ['method'],
    computed: {
      icon() {
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
              return 'face'
            default:
              console.error('method unkonwn' + this.method);
          }
        }
        return '';
      },
      color() {
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
        }
        return '';
      }
    }
  }
</script>
