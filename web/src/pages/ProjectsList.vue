<template>
  <div class="projects-page page-list container">
    <header-app :help="txtHelp" :bc-datas="[{icon:'view_list', label:'Liste des Projets'}]"></header-app>

    <q-card>
      <q-card-title>
        <h3>Liste des Projets</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <transition
          appear
          enter-active-class="animated fadeIn"
          leave-active-class="animated fadeOut"
        >
          <div v-show="!loading">
            <q-table
              :data="list"
              :columns="columns"
              row-key="field"
              :filter="filter"
              :separator="separator"
              :no-data-label="noData"
              :pagination.sync="pagination"
              :no-results-label="noDataAfterFiltering"
              :rowsPerPageOptions="rowsPerPageOptions"
              @refresh="refresh"
            >
              <template slot="top-left" slot-scope="props">
                <q-search
                  v-model="filter"
                  class="col-auto"
                />
              </template>
              <template slot="top-right" slot-scope="props">
                <q-select
                  color="secondary"
                  v-model="separator"
                  :options="separatorOptions"
                  hide-underline
                />
              </template>
              <q-td slot="body-cell-javaDeps" slot-scope="props" :props="props">
                <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openInfos(props.value, 'Dépendance Java')"
                   class="tooltip">
                  <span>{{props.value.length}}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{props.value.length}}&nbsp;</span>
              </q-td>
              <q-td slot="body-cell-apis" slot-scope="props" :props="props">
                <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openInfos(props.value, 'Apis')"
                   class="tooltip">
                  <span>{{props.value.length}}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{props.value.length}}&nbsp;</span>
              </q-td>
              <q-td slot="body-cell-tables" slot-scope="props" :props="props">
                <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openInfos(props.value, 'Tables')"
                   class="tooltip">
                  <span>{{props.value.length}}&nbsp;</span>
                  <i class="material-icons">info </i>
                </a>
                <span v-else>{{props.value.length}}&nbsp;</span>
              </q-td>
              <q-td slot="body-cell-changelog" slot-scope="props" :props="props">
                <changelog-button :key="props.row.id" :content="props.value"/>
              </q-td>
              <q-td slot="body-cell-destinationUrl" slot-scope="props" :props="props">
                <q-btn flat color="tertiary" @click="$router.push(props.value)" small>
                  <q-icon name="description"/>
                </q-btn>
              </q-td>
            </q-table>
          </div>
        </transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
        </q-inner-loading>
      </q-card-main>
    </q-card>
    <q-modal v-model="modal" :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            {{modalOpt.title}}
          </div>
          <q-btn flat @click="modal = false">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div>
          <q-list highlight>
            <q-item v-for="d in modalOpt.data" :key="d">
              <q-item-main>{{d}}</q-item-main>
            </q-item>
          </q-list>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import txtHelp from './projects/help.md'
  import ChangelogButton from '../components/ChangeLogButton'
  import ProjectsStore from '../stores/ProjectsStore';
  import {formatYYYYMMDDHHmm} from '../Dates';
  import filtering from '../FiltersAndSorter'
  import HeaderApp from '../components/HeaderApp';
  import {
    noData,
    noDataAfterFiltering,
    separator,
    separatorOptions,
    pagination,
    rowsPerPageOptions
  } from '../datatable-utils'

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
      HeaderApp,
      ChangelogButton,
    },
    data() {
      return {
        txtHelp,
        list: [],
        original: [],
        filter: '',
        modal: false,
        modalOpt: {},
        loading: false,
        separator,
        separatorOptions,
        pagination,
        noData,
        noDataAfterFiltering,
        rowsPerPageOptions,
        columns: [
          {
            label: 'Nom',
            field: 'name',
            name: 'name',
            sortable: true,
            type: 'string',
            filter: true
          },
          {
            label: 'Snapshot',
            field: 'snapshot',
            name: 'snapshot',
            sortable: false,
            type: 'string',
            filter: true
          },
          {
            label: 'Release',
            field: 'release',
            name: 'release',
            sortable: false,
            type: 'string',
            filter: true
          },
          {
            label: 'Java',
            field: 'javaDeps',
            name: 'javaDeps',
            sort(a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: true
          },
          {
            label: 'Apis',
            field: 'apis',
            name: 'apis',
            sort(a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: true
          },
          {
            label: 'Tables',
            field: 'tables',
            name: 'tables',
            sort(a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: true
          },
          {
            label: 'Dernière Mise à jour',
            field: 'latest',
            name: 'latest',
            sortable: true,
            type: 'date',
            filter: true
          },
          {
            label: 'Log',
            field: 'changelog',
            name: 'changelog',
            sortable: false
          },
          {
            label: 'Détail',
            field: 'destinationUrl',
            name: 'destinationUrl',
            sortable: false
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
<style lang="stylus" scoped>
  .projects-page
    .q-table-control
      .q-search
        min-width 30vw
</style>
