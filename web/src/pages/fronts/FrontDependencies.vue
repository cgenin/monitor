<template>
  <div class="dependencies-page page-list container">
    <header-app :bc-datas="[{icon:'link', label:'Dépendances service -> front'}]"></header-app>
    <q-card>
      <q-card-title>
        <h3>Dépendances service -> front</h3>
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
  import HeaderApp from '../../components/HeaderApp';
  import { FrontDependencies } from '../../Routes';

  import { loadServices, namespace, services } from '../../store/fronts/constants';

  const frontsStore = createNamespacedHelpers(namespace);

  export default {
    name: 'FrontDependencies',
    components: {
      HeaderApp,
    },
    data() {
      return {
        terms: null,
      };
    },
    computed: {
      ...frontsStore.mapGetters([services]),
    },
    methods: {
      search(terms, done) {
        const t = terms.toUpperCase();
        done(filter(t, { field: 'value', list: this.services }));
      },
      selected(result) {
         this.$router.push(`${FrontDependencies}/search/${result.value.toUpperCase()}`);
      },
      reset() {
        this.terms = '';
        this.$router.push((FrontDependencies));
      },
      ...frontsStore.mapActions([loadServices]),
    },
    mounted() {
      this.loadServices()
        .then(() => {
          const { resource } = this.$router.history.current.params;
          if (resource) {
            this.terms = resource;
          }
        });
    },
  };
</script>
<style lang="stylus">
  @import "../../css/pages/dependencies.styl"
</style>
