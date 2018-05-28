<template>
  <div class="projects-page page-list container">
    <header-app :bc-datas="[{icon:'view_list', label:'Liste des Projets NPM'}]"></header-app>

    <q-card>
      <q-card-title>
        <h3>Liste des Projets NPM</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div v-if="list">
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
            <q-td slot="body-cell-readme" slot-scope="props" :props="props">
              <markdown-button title="Readme" color="tertiary" icon="description" :key="props.row.id" :content="props.value" v-if="props.value !== 'ERROR: No README data found!'"></markdown-button>
            </q-td>
            <q-td slot="body-cell-infos" slot-scope="props" :props="props">
              <markdown-button title="Infos" icon="info" :key="props.row.id" :content="props.value"></markdown-button>
            </q-td>
          </q-table>
        </div>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import MarkdownButton from '../../components/MarkdownButton'
  import HeaderApp from '../../components/HeaderApp';
  import NpmStore from '../../stores/NpmStore';
  import filtering from '../../FiltersAndSorter'
  import {
    noData,
    noDataAfterFiltering,
    separator,
    separatorOptions,
    pagination,
    rowsPerPageOptions
  } from '../../datatable-utils'
  import {formatYYYYMMDDHHmm} from '../../Dates';

  export default {
    name: 'NpmList',
    components: {
      HeaderApp,
      MarkdownButton
    },
    data() {
      return {
        list: null,
        selected: null,
        loading: false,
        filter : '',
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
            width: '270px',
            sortable: true,
            type: 'string',
            filter: true
          },
          {
            label: 'Version',
            field: 'latestVersion',
            name: 'latestVersion',
            sortable: true,
            type: 'string',
            filter: true
          },
          {
            label: 'Build time',
            field: 'latestBuildTime',
            name: 'latestBuildTime',
            sortable: true,
            type: 'string',
            filter: true
          },
          {
            label: 'Readme',
            field: 'readme',
            name: 'readme',
            width: '57px',
            sortable: true,
          },
          {
            label: 'Détail',
            field: 'infos',
            name: 'infos',
            width: '67px',
            sortable: false
          }
        ]
      }
    },
    methods: {
      async refresh() {
        this.filter = '';
        this.list = this.objectAsArray(await NpmStore.getList())
          .filter((component) => component.name)
          .map((el) => {
            let latestBuild = (el['dist-tags'] || {}).latest;
            return {
              ...el,
              latestVersion : latestBuild,
              latestBuildTime : formatYYYYMMDDHHmm(new Date(el.time[latestBuild])),
              infos : this.createInfos(el)
            }
          })
          .sort((a, b) => a.name.localeCompare(b.name));
      },
      objectAsArray(obj) {
        return Object.keys(obj).map((key) => obj[key]);
      },
      async selectComponent(component) {
        this.loading = true;
        try {
          this.selected = await NpmStore.getComponentInfos(component);
        } catch (e) {
          console.error(e);
        }
        this.loading = false;
      },
      filtering() {
        this.list = filtering(this.original, this.filter);
      },
      createInfos(component){
        return `
    ## Description
    ${component.description}
    ## Versions
        ` +
        Object.values(component.versions).reverse().reduce((a,b) => `
    ${a}
    ### ${b}
    Build time : ${component.time[b] ? formatYYYYMMDDHHmm(component.time[b]) : ''}
    `,'');
      },
    },
    async mounted() {
      this.refresh();
    },
  }
</script>
<style>
</style>
