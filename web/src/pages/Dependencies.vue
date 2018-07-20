<template>
  <div class="dependencies-page page-list container">
    <header-app :bc-datas="[{icon:'link', label:'Dépendance Intra Service'}]"></header-app>
    <q-card>
      <q-card-title>
        <h3>Dépendance Intra Service</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div class="noprint row inputs">
          <div class="label-container col-sm-2 col-xs-12">
            <label>Les projets qui nécessitent</label>
          </div>
          <q-field class="col-sm-7 col-xs-12" icon="search">
            <q-input color="secondary" v-model="terms"
                     placeholder="Sélectionner la ressource">
              <q-autocomplete
                @search="search"
                :min-characters="1"
                @selected="selected"
              />
            </q-input>
          </q-field>
          <div class="row col-sm-2 col-xs-12">
            <q-btn class="btn-flat-primary full-width" @click="reset" flat>Reset</q-btn>
          </div>
        </div>
      </q-card-main>
    </q-card>

    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { filter } from 'quasar';
  // import DependenciesStore from '../stores-old/DependenciesStore';
  import HeaderApp from '../components/HeaderApp';
  import { initialize, namespace, resources } from '../store/dependencies/constants';

  const dependenciesStore = createNamespacedHelpers(namespace);

  export default {
    name: 'Configuration',
    components: {
      HeaderApp,
    },
    data() {
      return {
        terms: null,
      };
    },
    computed: {
      ...dependenciesStore.mapGetters([resources]),
    },
    methods: {
      search(terms, done) {
        const t = terms.toUpperCase();
        done(filter(t, { field: 'value', list: this.resources }));
      },
      selected(result) {
        this.$router.push(`/dependencies/search/${result.value.toUpperCase()}`);
      },
      reset() {
        this.terms = '';
        this.$router.push('/dependencies');
      },
      ...dependenciesStore.mapActions([initialize]),
    },
    mounted() {
      this.initialize().then(() => {
        const { resource } = this.$router.history.current.params;
        if (resource) {
          this.terms = resource;
        }
      });
    },
  };
</script>
<style lang="stylus">
  @import "../css/pages/dependencies.styl"
</style>
