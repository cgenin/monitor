<template>
  <li @click="click" :class="{nopointer:notOpens, withpointer:!notOpens}">
    <p>
      <i v-if="!notOpens && open" class="fas fa-minus-circle"></i>
      <i v-if="!notOpens && !open" class="fas fa-plus-circle"></i>
      {{resource}}
    </p>
    <ul v-if="open">
      <sub-tree v-for="sub in subs" :key="sub" :resource="sub"/>
    </ul>
  </li>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { nameModule, usedBy, dependencies } from '../store/dependencies/constants';

  const dependenciesStore = createNamespacedHelpers(nameModule);


  export default {
    name: 'SubTree',
    props: ['resource'],
    data() {
      return { open: false, subs: [], notOpens: false };
    },
    computed: {
      ...dependenciesStore.mapGetters([dependencies]),
    },
    methods: {
      click() {
        if (this.notOpens) {
          return;
        }

        this.open = !this.open;
        if (this.open) {
          this.usedBy(this.resource)
            .then((subs) => {
              this.subs = subs;
            });
        } else {
          this.subs = [];
        }
      },
      ...dependenciesStore.mapActions([usedBy]),
    },
    mounted() {
      this.notOpens = this.dependencies[this.resource];
    },
  };
</script>
