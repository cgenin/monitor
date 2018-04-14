<template>
  <li @click="click" :class="{nopointer:notOpens, withpointer:!notOpens}">
    <p>
      <il v-if="!notOpens && open" class="fa fa-minus-circle"></il>
      <il v-if="!notOpens && !open" class="fa fa-plus-circle"></il>
      {{resource}}
    </p>
    <ul v-if="open">
      <sub-tree v-for="sub in subs" :key="sub" :resource="sub"/>
    </ul>
  </li>
</template>
<script>
  import DependenciesStore from '../stores/DependenciesStore';

  export default {
    name: 'SubTree',
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
    }
  }
</script>
