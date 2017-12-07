<template>
  <div class="tables-page">
    <q-card>
      <q-card-title>
        <h4>Liste des Tables</h4>
      </q-card-title>

      <q-card-separator/>
      <q-card-main>
        <div class="inputs">
          <q-input v-model="filter" type="text" class="filter" float-label="filter" @change="filtering"></q-input>
          <q-btn round color="primary" icon="refresh" @click="refresh"></q-btn>
        </div>
        <q-card-separator/>
        <q-transition
          appear
          enter="fadeIn"
          leave="fadeOut"
        >

          <div v-if="!loading" style="width:100%">
            <div class="font-result results-number">
              <strong>Résultats : {{list.length}}</strong>
            </div>
            <div>
              <table class="font-result q-table highlight responsive results">
                <thead>
                <tr>
                  <th>Nom</th>
                  <th>Projet(s) Lié(s)</th>
                  <th>Dernière Mise à jour</th>
                </tr>
                </thead>
                <tbody class="text-secondary">
                <tr v-for="table in list">
                  <td>
                    {{table.name}}
                  </td>
                  <td>
                    <ul>
                      <li v-for="s in table.services">
                        {{s}}
                      </li>
                    </ul>
                  </td>
                  <td>
                    {{table.latest}}
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </q-transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
        </q-inner-loading>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import {
    QCard, QCardTitle, QCardSeparator, QCardMain, QInput, QBtn, QInnerLoading, QTransition, QSpinnerGears
  } from 'quasar';
  import TablesStore from '../stores/TablesStore';
  import {format} from '../Dates';
  import filtering from '../Filters'

  const toServicesStr = (services) => {
    if (!services || services.length === 0) {
      return '';
    }
    if (services.length === 1) {
      return services[0];
    }

    return services.reduce((a, b) => a + ', ' + b);
  };

  export default {
    name: 'TablesList',
    components: {
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInput,
      QBtn,
      QInnerLoading,
      QTransition,
      QSpinnerGears
    },
    data() {
      return {list: [], original: [], filter: '', loading: false};
    },
    methods: {
      refresh() {
        this.loading = true;
        TablesStore.initialize()
          .then(res => {
            const l = res
              .map(o => {
                const latest = format(o.latestUpdate);
                const serviceStr = toServicesStr(o.services);
                return Object.assign({}, o, {latest, serviceStr});
              })
              .sort((a, b) => {
                return a.name.localeCompare(b.name);
              });
            this.list = l;
            this.original = l;
            this.filter = '';
            setTimeout(() => {
              this.loading = false;
            }, 300);
          });
      },
      filtering() {
        this.list = filtering(this.original, this.filter);
      }
    },
    mounted() {
      this.refresh();
    }
  }
</script>
<style scoped>
  .tables-page {

  }

  .tables-page .font-result {
    font-size: 1.3rem;
    line-height: inherit;
  }

  .tables-page .results-number {
    margin-top: .5em;
  }

  .tables-page .inputs {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .tables-page .inputs .filter {
    width: 50%;
  }

  .tables-page table.results {
    margin: auto;
    width: 100%;
  }

</style>
