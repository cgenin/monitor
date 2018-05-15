<template>
  <div class="projects-page page-list">
    <header-app :bc-datas="[{icon:'view_list', label:'Liste des Projets NPM'}]"></header-app>

    <q-card class="container">
      <q-card-title>
        <h3>Comparaison de serveurs</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div>
          <br>
          Sélectionner un (ou plusieurs) serveurs :
          <br>
        </div>

        <div>
          <q-select
            v-model="serverList"
            :options="servers"
            @input="serverChanged"
            v-if="servers"
            multiple
          >
          </q-select>
        </div>
        <div v-if="services">
          <q-inner-loading :visible="loading">
            <q-spinner-gears size="50px" color="primary"/>
          </q-inner-loading>
          <q-list v-if="columns.length > 1"
          >
            <q-table
              :data="services"
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
              <q-td slot="body-cell-compare" slot-scope="props" :props="props">
                <div
                  v-if="servicesByServer[props.col.label][props.value].info && servicesByServer[props.col.label][props.value].info.build">
                  <charts-button :serviceInfos="servicesByServer[props.col.label][props.value]"
                                 :serviceName="props.value" :server="props.col.label"></charts-button>
                </div>
                <span
                  v-if="!servicesByServer[props.col.label][props.value].responding">
                  <q-item-side class="down" icon="portable_wifi_off"/>
                </span>
                <span
                  v-if="!(servicesByServer[props.col.label][props.value].info && servicesByServer[props.col.label][props.value].info.build) && servicesByServer[props.col.label][props.value].responding">
                  <q-item-side class="up" icon="graphic_eq"/> Up but no infos...
                </span>
              </q-td>
            </q-table>
          </q-list>
        </div>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import ConfigurationStore from '../../stores/ConfigurationStore';
  import ChangelogButton from '../../components/ChangeLogButton';
  import ChartsButton from '../../components/ChartsButton';
  import HeaderApp from '../../components/HeaderApp';
  import filtering from '../../FiltersAndSorter';
  import {
    noData,
    noDataAfterFiltering,
    pagination,
    rowsPerPageOptions,
    separator,
    separatorOptions
  } from '../../datatable-utils'

  export default {
    name: 'ServeursCompare',
    components: {
      HeaderApp,
      ChangelogButton,
      ChartsButton
    },
    data() {
      return {
        serverList: [],
        services: [],
        servicesByServer: [],
        selected: null,
        loading: false,
        filter: '',
        separator,
        separatorOptions,
        pagination,
        noData,
        noDataAfterFiltering,
        rowsPerPageOptions,
        servers: null,
        server: null,
        columns: [],
        runningPromise: null,
        cancelRunningPromise: false,
        moniThorUrl: null
      }
    },
    methods: {
      async refresh() {
        this.filter = '';
      },
      objectAsArray(obj) {
        return Object.keys(obj).map((key) => obj[key]);
      },
      serverChanged(servers) {
        this.cancelRunningPromise = !!this.runningPromise;
        this.loading = true;
        this.columns = [{
          label: 'Services',
          field: 'serviceName',
          name: 'serviceName',
          type: 'string',
          filter: true,
          align: 'left'
        }];
        const responsePromises = [];
        for (let i in servers) {
          const server = servers[i];
          responsePromises.push(fetch(`${this.moniThorUrl}/api/services/ping/all?server=${server}`)
            .then((res) => res.json())
            .then((services) => {
              this.servicesByServer[server] = services.reduce((a, b) => {
                  a[b.serviceName] = b;
                  return a;
                }, {}
              );
              this.services = services.map((s) => {
                return {serviceName: s.serviceName};
              }).sort((a, b) => a.serviceName.localeCompare(b.serviceName));
              return server;
            })
          );
        }
        this.runningPromise = Promise.all(responsePromises)
          .then((servers) => {
            if (!this.cancelRunningPromise) {
              servers.forEach((server) => {
                if (this.serverList.includes(server) && this.columns.filter((c) => c.label === server).length === 0) {
                  this.columns.push({
                    label: server,
                    field: 'serviceName',
                    name: 'compare',
                    sortable: true,
                    type: 'string',
                    filter: true,
                    align: 'left'
                  });
                }
              });
            } else {
              this.cancelRunningPromise = false;
            }
          })
          .catch((error) => {
            console.error(error);
          })
          .then(() => {
            this.loading = false;
            this.runningPromise = null;
          })
      },
      filtering() {
        this.services = filtering(this.original, this.filter);
      },
    },
    mounted() {
      this.pagination.rowsPerPage = 50;
      this.moniThorUrl = null;
      ConfigurationStore.initialize()
        .then(() => {
          this.moniThorUrl = ConfigurationStore.moniThorUrl;

          fetch(`${this.moniThorUrl}/api/servers`)
            .then((res) => res.json())
            .then((serversWrappers) => this.servers = serversWrappers.servers.map(s => {
              return {
                label: s,
                value: s
              };
            }));
        });
      this.refresh();
    }
  }
</script>
<style>
  .q-item:hover {
    background-color: #f2f4f8;
    cursor: pointer;
  }

  .up {
    color: #00cb4b;
  }

  .down {
    color: #cb2832;
  }
</style>