<template>
  <div class="page-list">
    <q-card class="container">
      <q-card-main>
        <q-transition
          appear
          enter="fadeIn"
          leave="fadeOut"
        >
          <div v-if="!loading" style="width:100%">
            <div>
              <q-data-table
                :data="list"
                :config="config"
                :columns="columns"
                @refresh="refresh">
                <template slot="col-services" slot-scope="cell">
                  <ul>
                    <li v-for="s in cell.data">{{s}}</li>
                  </ul>
                </template>
              </q-data-table>
            </div>
          </div>
        </q-transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"/>
        </q-inner-loading>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import {
    QCard, QCardTitle, QCardSeparator, QCardMain, QInnerLoading, QTransition, QSpinnerGears,
    QDataTable
  } from 'quasar';
  import TablesStore from '../../stores/TablesStore';
  import {format} from '../../Dates';
  import filtering, {sortString} from '../../FiltersAndSorter'

  export default {
    name: 'TablesList',
    components: {
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInnerLoading,
      QTransition,
      QSpinnerGears,
      QDataTable
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
            type: 'string',
            filter: true
          },
          {
            label: 'Projet(s) lié(s)',
            field: 'services',
            width: '380px',
            filter: true,
            sort(a, b) {
              if (a.length === b.length) {
                return sortString(a[0], b[0]);
              }
              return a.length - b.length;
            }
          },
          {
            label: 'Dernière Mise à jour',
            field: 'latest',
            width: '230px',
            sort: true,
            type: 'date',
            filter: true
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
                return Object.assign({}, o, {latest});
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
