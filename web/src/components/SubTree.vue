<template>
  <li @click="click" :class="{nopointer:notOpens, withpointer:!notOpens}">
    <p>
      {{label}}
    </p>
    <ul v-if="open">
      <sub-tree v-for="sub in subs" :key="sub" :resource="sub"/>
    </ul>
  </li>
</template>
<script>
  import {QIcon} from 'quasar';
  import DependenciesStore from '../stores/DependenciesStore';

  export default {
    name: 'SubTree',
    components: {
      QIcon
    },
    props: ['resource'],
    data() {
      return {open: false, subs: [], notOpens: false};
    },
    methods: {
      click() {
        if (this.notOpens) {
          return;
        }

        this.open = !this.open;
        if (this.open) {
          DependenciesStore.usedBy(this.resource)
            .then(subs => {
              this.subs = subs;
            });
        }
        else {
          this.subs = [];
        }
      }
    },
    mounted() {
      this.notOpens = DependenciesStore.dependencies[this.resource];
    },
    computed: {
      label() {
        if (this.notOpens) {
          return this.resource;
        }
        if (this.open) {
          return `${this.resource} -`;
        }
        return `${this.resource} +`;
      }
    }

  }
</script>
