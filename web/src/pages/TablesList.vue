<template>
  <div class="tables-page page-list">
    <q-card>
      <q-card-title>
        <h4>Liste des Tables</h4>
      </q-card-title>

      <q-card-separator/>
      <q-card-main>
        <!--<div class="inputs">-->
          <!--<q-input v-model="filter" type="text" class="filter" float-label="filter" @change="filtering"></q-input>-->
          <!--<q-btn round color="primary" icon="refresh" @click="refresh"></q-btn>-->
        <!--</div>-->
        <!--<q-card-separator/>-->
        <q-transition
          appear
          enter="fadeIn"
          leave="fadeOut"
        >

          <div v-if="!loading" style="width:100%">
            <!--<div class="font-result results-number">-->
              <!--<strong>Résultats : {{list.length}}</strong>-->
            <!--</div>-->
            <div>
              <q-data-table
                :data="list"
                :config="config"
                :columns="columns"
                @refresh="refresh">

              </q-data-table>
              <!--<table class="font-result q-table striped-odd bordered vertical-separator highlight responsive results">-->
                <!--<thead>-->
                <!--<tr>-->
                  <!--<th>Nom</th>-->
                  <!--<th>Projet(s) Lié(s)</th>-->
                  <!--<th>Dernière Mise à jour</th>-->
                <!--</tr>-->
                <!--</thead>-->
                <!--<tbody>-->
                <!--<tr v-for="table in list">-->
                  <!--<td>-->
                    <!--{{table.name}}-->
                  <!--</td>-->
                  <!--<td>-->
                    <!--<ul>-->
                      <!--<li v-for="s in table.services">-->
                        <!--{{s}}-->
                      <!--</li>-->
                    <!--</ul>-->
                  <!--</td>-->
                  <!--<td>-->
                    <!--{{table.latest}}-->
                  <!--</td>-->
                <!--</tr>-->
                <!--</tbody>-->
              <!--</table>-->
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
    QCard, QCardTitle, QCardSeparator, QCardMain, QInput, QBtn, QInnerLoading, QTransition, QSpinnerGears,
    QDataTable, QField, QCollapsible
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
      QSpinnerGears,
      QDataTable,
      QField,
      QCollapsible
    },
    data() {
      return {
        list: [],
        tableDatas: [],
        original: [],
        filter: '',
        loading: false,
        config: {
          title: 'Liste des tables et projets liés',
          refresh: true,
          noHeader: false,
          bodyStyle: {
            maxHeight: '500px'
          },
          rowHeight: '50px',
          responsive: true,
          pagination: {
            rowsPerPage: 15,
            options: [5, 10, 15, 30, 50, 500]
          },
          messages: {
            noData: '<i>Attention</i> aucune donnée disponible.',
            noDataAfterFiltering: '<i>Attention</i> aucun résultat. Veuillez affiner votre recherche.'
          },
          labels: {
            columns: 'Colonnes',
            allCols: 'Toutes les colonnes',
            rows: 'Résultats',
            clear: 'réinitialiser',
            search: 'Filtrer',
            all: 'Tous'
          }
        },
        columns: [
          {
            label: 'Nom',
            field: 'name',
            width: '400px',
            sort: true,
            type: 'string'
          },
          {
            label: 'Projet(s) lié(s)',
            field: 'serviceStr',
            width: '380px',
            sort: true,
            type: 'string'
          },
          {
            label: 'Dernière Mise à jour',
            field: 'latest',
            width: '230px',
            sort: true,
            type: 'date'
          }
        ]
      };
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
              });;
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

</style>
