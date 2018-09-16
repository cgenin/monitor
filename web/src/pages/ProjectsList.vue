<template>
  <div class="projects-page page-list container">
    <header-app :help="txtHelp" :bc-datas="[{icon:'view_list', label:'Liste des Projets'}]"></header-app>

    <q-card>
      <q-card-title>
        <h3>Liste des Projets</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div class="search-bar">
          <q-search
            v-model="filter"
            :debounce="300"
            placeholder="Rechercher par nom"
            class="col-auto"
            @input="filtering"
          ></q-search>
        </div>
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
              :separator="separator"
              :no-data-label="noDataAfterFiltering"
              :pagination.sync="pagination"
              :rowsPerPageOptions="rowsPerPageOptions"
              @refresh="refresh"
            >
              <template slot="top-right" slot-scope="props">
                <q-select
                  color="secondary"
                  v-model="separator"
                  :options="separatorOptions"
                  hide-underline
                ></q-select>
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
                  <q-icon name="description"></q-icon>
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
  import { createNamespacedHelpers } from 'vuex';
  import { loadProjects, namespace as namespaceMicroService, projects } from '../store/microservices/constants';
  import txtHelp from './projects/help.md';
  import ChangelogButton from '../components/ChangeLogButton';
  import HeaderApp from '../components/HeaderApp';
  import { noDataAfterFiltering, pagination, rowsPerPageOptions, separator, separatorOptions } from '../datatable-utils';

  const microServicesStore = createNamespacedHelpers(namespaceMicroService);


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
        filter: '',
        modal: false,
        modalOpt: {},
        loading: false,
        separator,
        separatorOptions,
        pagination,
        noDataAfterFiltering,
        rowsPerPageOptions,
        columns: [
          {
            label: 'Nom',
            field: 'name',
            name: 'name',
            sortable: true,
            type: 'string',
            filter: false,
          },
          {
            label: 'Snapshot',
            field: 'snapshot',
            name: 'snapshot',
            sortable: false,
            type: 'string',
            filter: false,
          },
          {
            label: 'Release',
            field: 'release',
            name: 'release',
            sortable: false,
            type: 'string',
            filter: false,
          },
          {
            label: 'Java',
            field: 'javaDeps',
            name: 'javaDeps',
            sort(a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: false,
          },
          {
            label: 'Apis',
            field: 'apis',
            name: 'apis',
            sort(a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: false,
          },
          {
            label: 'Tables',
            field: 'tables',
            name: 'tables',
            sort(a, b) {
              return (a.length - b.length);
            },
            type: 'number',
            filter: false,
          },
          {
            label: 'Dernière Mise à jour',
            field: 'latest',
            name: 'latest',
            sortable: true,
            type: 'date',
            filter: false,
          },
          {
            label: 'Log',
            field: 'changelog',
            name: 'changelog',
            sortable: false,
          },
          {
            label: 'Détail',
            field: 'destinationUrl',
            name: 'destinationUrl',
            sortable: false,
          },
        ],
      };
    },
    computed: {
      ...microServicesStore.mapGetters([projects]),
    },
    methods: {
      refresh() {
        this.loading = true;
        this.loadProjects()
          .then(() => {
            this.list = this.projects;
            this.filter = '';
            this.loading = false;
          });
      },
      openInfos(p, title) {
        const data = p.sort();
        this.modalOpt = {
          data, title,
        };
        this.modal = true;
      },
      filtering() {
        const { filter } = this;
        if (!filter || filter.trim() === '') {
          this.list = this.projects;
          return;
        }

        const upFilter = filter.toUpperCase();
        this.list = this.projects
          .filter((p) => {
            const { name } = p;
            const upName = name.toUpperCase();
            return (upName.indexOf(upFilter) !== -1);
          });
      },
      ...microServicesStore.mapActions([loadProjects]),
    },
    mounted() {
      this.refresh();
    },
  };
</script>
<style lang="stylus" scoped>
  .projects-page
    .search-bar
      display flex
      justify-content center
      margin 5px 5px
      .q-search
        min-width 30vw
</style>
