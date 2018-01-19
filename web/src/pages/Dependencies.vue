<template>
  <div class="dependencies-page page-list">
    <ul class="breadcrumb noprint">
      <li>
        <router-link to="/">
          <q-icon name="home"/>
        </router-link>
      </li>
      <li>
        <router-link to="" active-class="router-link-active">
          <q-icon name="link"/>
          Dépendance Intra Service
        </router-link>
      </li>
    </ul>
    <q-card class="container">
      <q-card-title>
        <h3>Dépendance Intra Service</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div class="noprint row inputs">
          <div class="label-container col-sm-2 col-xs-12">
            <label>Les projets qui nécessitent :</label>
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
          <div class="col-sm-2 col-xs-12">
            <q-btn class="btn-flat-primary" @click="reset" flat>Reset</q-btn>
          </div>
        </div>
      </q-card-main>
    </q-card>

    <router-view :key="$route.fullPath"></router-view>
  </div>
</template>
<script>
  import {
    QCard,
    QCardTitle,
    QCardSeparator,
    QCardMain,
    QInput,
    QBtn,
    QList,
    QItem,
    QIcon,
    QAutocomplete,
    QField,
    QToggle,
    QItemMain,
    QSpinnerDots,
    filter
  } from 'quasar';
  import DependenciesStore from '../stores/DependenciesStore'

  export default {
    name: 'Configuration',
    components: {
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QAutocomplete,
      QInput,
      QBtn,
      QList,
      QItem,
      QItemMain,
      QIcon,
      QField,
      QToggle,
      QSpinnerDots
    },
    data() {
      return {
        terms: null,
        resources: []
      };
    },
    methods: {
      search(terms, done) {
        const t = terms.toUpperCase();
        done(filter(t, {field: 'value', list: this.resources}))
      },
      selected(result) {
        console.log(result);
        this.$router.push(`/dependencies/search/${result.value.toUpperCase()}`);
      },
      reset() {
        this.terms = '';
        this.$router.push(`/dependencies`)
      }
    },
    mounted() {
      DependenciesStore.initialize().then(
        rs => {
          this.resources = rs.map(resource => {
            return {
              label: resource.toLowerCase(),
              sublabel: 'Java',
              value: resource.toLowerCase()
            };
          });
        }
      );
    }
  }
</script>
<style lang="stylus">
  @import "dependencies/dependencies.styl"
</style>
