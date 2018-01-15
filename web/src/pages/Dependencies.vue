<template>
  <div class="dependencies-page page-list">
    <q-card>
      <ul class="breadcrumb noprint">
        <li>
          <router-link to="/">
            <q-icon name="home"/>
          </router-link>
        </li>
        <li>
          <router-link to="" active-class="router-link-active">
            <q-icon name="explore"/>
            Dépendance Intra Service
          </router-link>
        </li>
      </ul>
    </q-card>
    <q-card>
      <q-card-title>
        <h3>Dépendance Intra Service</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div class="noprint row justify-center content-center">
          <q-field class="col-sm-6 col-xs-12" icon="search">
            <q-input color="secondary" v-model="terms"
                     placeholder="Sélectionner la ressource">
              <q-autocomplete
                @search="search"
                :min-characters="1"
                @selected="selected"
              />
            </q-input>
          </q-field>
          <div class="col-sm-2 col-xs-12 buttons">
            <q-btn @click="reset" round color="blue">Reset</q-btn>
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
<style>
  .dependencies-page .buttons {
    text-align: center;
  }

  @media print {
    .dependencies-page .breadcrumb {
      display: none;
    }
    .dependencies-page  .q-card {
      box-shadow: none;
    }

    .dependencies-page .q-card-main {
      display: none;
    }
  }
</style>
