<template>
  <div class="projects-page page-list">
    <ul class="breadcrumb">
      <li>
        <router-link to="/">
          <q-icon name="home" />
        </router-link>
      </li>
      <li>
        <router-link to="" active-class="router-link-active">
          <q-icon name="view_list" /> Liste des Projets
        </router-link>
      </li>
    </ul>
    <q-card class="container">
      <q-card-title>
        <h3>Liste des Projets</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <q-transition
          appear
          enter="fadeIn"
          leave="fadeOut"
        >
          <div v-show="!loading">
            <q-data-table
              :data="list"
              :config="config"
              :columns="columns"
              @refresh="refresh"
            >
              <template v-if="cell.data" slot="col-javaDeps" slot-scope="cell">
                <a href="#" v-if="cell.data.length > 0" v-on:click.prevent="openInfos(cell.data, 'Dépendance Java')" class="tootip">
                  <span>{{cell.data.length}}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{cell.data.length}}&nbsp;</span>
              </template>
              <template v-if="cell.data" slot="col-apis" slot-scope="cell">
                <a href="#" v-if="cell.data.length > 0" v-on:click.prevent="openInfos(cell.data, 'Apis')" class="tootip">
                  <span>{{cell.data.length}}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{cell.data.length}}&nbsp;</span>
              </template>
              <template v-if="cell.data" slot="col-tables" slot-scope="cell">
                <a href="#" v-if="cell.data.length > 0" v-on:click.prevent="openInfos(cell.data, 'Tables')" class="tootip">
                  <span>{{cell.data.length}}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{cell.data.length}}&nbsp;</span>
              </template>
              <template v-if="cell.data" slot="col-changelog" slot-scope="cell">
                <changelog-button :content="cell.data"></changelog-button>
              </template>
              <template v-if="cell.data" slot="col-destinationUrl" slot-scope="cell">
                <q-btn flat color="tertiary" @click="$router.push(cell.data)" small>
                  <q-icon name="ion-document-text" />
                </q-btn>
              </template>
            </q-data-table>
          </div>
        </q-transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
        </q-inner-loading>
      </q-card-main>
    </q-card>
    <q-modal ref="layoutModal" v-model="modal" :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            {{modalOpt.title}}
          </div>
          <q-btn flat @click="$refs.layoutModal.close()">
            <q-icon name="close" />
          </q-btn>
        </q-toolbar>
        <div>
          <q-list highlight>
            <q-item v-for="d in modalOpt.data" key="d">
              <q-item-main>{{d}}</q-item-main>
            </q-item>
          </q-list>
        </div>
      </q-modal-layout>
    </q-modal>
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
    QTooltip,
    QToolbar,
    QModal,
    QModalLayout,
    QList,
    QItem,
    QItemMain,
    QInnerLoading,
    QTransition,
    QSpinnerGears,
    QDataTable,
    QIcon
  } from 'quasar';
  import ChangelogButton from '../components/ChangeLogButton'
  import ProjectsStore from '../stores/ProjectsStore';
  import {formatYYYYMMDDHHmm} from '../Dates';
  import filtering from '../Filters'

  const depTootip = (attr) => {
    if (!attr || attr.length === 0) {
      return '<p>Aucune dépendance.</p>';
    }
    return attr.map(v => `<ul><li>${v}</li></ul>`).reduce((a, b) => a + b, '');
  };
  const map = (l) => {
    if (!l) {
      return [];
    }
    return l.map(p => {
      p.destinationUrl = `/projects/${p.id}`;
      p.snapshot = p.snapshot || '-';
      p.release = p.release || '-';
      p.javaDeps = p.javaDeps || [];
      p.npmDeps = p.npmDeps || [];
      p.tables = p.tables || [];
      p.npmDepsTootip = depTootip(p.npmDeps);
      p.latest = formatYYYYMMDDHHmm(p.latestUpdate);
      return p;
    });
  };

  export default {
    name: 'ProjectsList',
    components: {
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInput,
      QBtn,
      QTooltip,
      QToolbar,
      QModal,
      QModalLayout,
      QList,
      QItem,
      QItemMain,
      ChangelogButton,
      QInnerLoading,
      QTransition,
      QSpinnerGears,
      QDataTable,
      QIcon
    },
    data() {
      return {
        list: [],
        original: [],
        filter: '',
        modal: false,
        modalOpt: {},
        loading: false,
        config: {
          refresh: true,
          columnPicker: false,
          noHeader: false,
          bodyStyle: {
            maxHeight: '500px'
          },
          rowHeight: '55px',
          rightStickyColumns: 2,
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
            selected: {
              singular: 'projet sélectionné.',
              plural: 'projets sélectionnés.'
            },
            clear: 'réinitialiser',
            search: 'Filtrer',
            all: 'Tous'
          }
        },
        columns: [
          {
            label: 'Nom',
            field: 'name',
            width: '270px',
            sort: true,
            type: 'string',
            filter: true
          },
          {
            label: 'Snapshot',
            field: 'snapshot',
            width: '170px',
            sort: false,
            type: 'string',
            filter: true
          },
          {
            label: 'Release',
            field: 'release',
            width: '90px',
            sort: false,
            type: 'string',
            filter: true
          },
          {
            label: 'Java',
            field: 'javaDeps',
            width: '73px',
            sort (a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: true
          },
          {
            label: 'Apis',
            field: 'apis',
            width: '73px',
            sort (a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: true
          },
          {
            label: 'Table',
            field: 'table',
            width: '73px',
            sort (a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: true
          },
          {
            label: 'Dernière Mise à jour',
            field: 'latest',
            width: '175px',
            sort: true,
            type: 'date',
            filter: true
          },
          {
            label: 'Log',
            field: 'changelog',
            width: '57px',
            sort: false
          },
          {
            label: 'Détail',
            field: 'destinationUrl',
            width: '67px',
            sort: false
          }
        ]
      };
    },
    methods: {
      refresh() {
        this.loading = true;
        ProjectsStore.initialize()
          .then((list) => {
            const res = map(list);
            this.list = res;
            this.original = res;
            this.filter = '';
            this.loading = false;
          });
      },
      openInfos(p, title) {
        const data = p.sort();
        this.modalOpt = {
          data, title
        };
        this.modal = true;
      },
      filtering() {
        console.log(this.filter)
        this.list = filtering(this.original, this.filter);
      },
      link() {

      }
    },
    mounted() {
      this.refresh();
    }
  }
</script>
