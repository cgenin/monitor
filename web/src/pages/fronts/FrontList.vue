<template>
  <div class="front-list-page page-list container">
    <header-app :bc-datas="[{icon:'fab fa-chrome', label:'Liste des Applications'}]"></header-app>

    <q-card>
      <q-card-title>
        <h3>Liste des Applications</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <transition
          appear
          enter-active-class="animated fadeIn"
          leave-active-class="animated fadeOut"
        >
          <q-table
            :data="frontApps"
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
            <q-td slot="body-cell-servicesClient" slot-scope="props" :props="props">
              <a href="#" v-if="props.value.length > 0" v-on:click.prevent="openServices(props.value)"
                 class="tooltip">
                <span>{{props.value.length}}&nbsp;</span>
                <i class="material-icons">info </i>
              </a>
              <span v-else>{{props.value.length}}&nbsp;</span>
            </q-td>
            <q-td slot="body-cell-packageJsonTxt" slot-scope="props" :props="props">
              <q-btn flat color="tertiary" @click="openPackageJson(props.value)" small>
                <q-icon name="description"/>
              </q-btn>
            </q-td>
          </q-table>
        </transition>
      </q-card-main>
    </q-card>
    <q-modal v-model="modal" :content-css="{minWidth: '75vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            Package.json
          </div>
          <q-btn flat @click="modal = false">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div>
          <pre>{{modalTxt}}</pre>
        </div>
      </q-modal-layout>
    </q-modal>
    <q-modal v-model="modalServices" :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            Micro-Services :
          </div>
          <q-btn flat @click="modalServices = false">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div>
          <q-list highlight>
            <q-item v-for="service in listServices" :key="service.key">
              <q-item-main>{{service.key}} : {{service.value}}</q-item-main>
            </q-item>
          </q-list>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { namespace as namespaceFronts, resume, loadResume } from '../../store/fronts/constants';
  import HeaderApp from '../../components/HeaderApp';
  import {
    noData,
    noDataAfterFiltering,
    separator,
    separatorOptions,
    pagination,
    rowsPerPageOptions,
  } from '../../datatable-utils';


  const frontsStore = createNamespacedHelpers(namespaceFronts);

  export default {
    name: 'FrontList',
    components: {
      HeaderApp,
    },

    data() {
      return {
        listServices: [],
        filter: '',
        modal: false,
        modalServices: false,
        modalTxt: '',
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
            align: 'left',
            sortable: true,
            type: 'string',
            filter: true,
          },
          {
            label: 'Dernière Mise à jour',
            field: 'lastUpdateDate',
            name: 'lastUpdateDate',
            sortable: false,
            align: 'left',
            type: 'string',
          },

          {
            label: 'Snapshot',
            field: 'snapshotVersion',
            name: 'snapshotVersion',
align: 'left',

            sortable: false,
            type: 'string',
            filter: true,
          },
          {
            label: '-',
            field: 'lastUpdateSnapshotDate',
            name: 'lastUpdateSnapshotDate',
            align: 'left',
            sortable: true,
            type: 'string',
          },
          {
            label: 'Release',
            field: 'releaseVersion',
            name: 'releaseVersion',
align: 'left',

            sortable: false,
            type: 'string',
            filter: true,
          },
          {
            label: '-',
            field: 'lastUpdateReleaseDate',
            name: 'lastUpdateReleaseDate',
            align: 'left',
            sortable: true,
            type: 'string',
          },
          {
            label: 'Services',
            field: 'servicesClient',
            name: 'servicesClient',
            sortable: false,
          },
          {
            label: 'Package.json',
            field: 'packageJsonTxt',
            name: 'packageJsonTxt',
            align: 'left',
            sortable: false,
          }],
      };
    },
    computed: {
      ...frontsStore.mapGetters({ frontApps: resume }),
    },
    methods: {
      refresh() {
        this.loadResume();
      },
      openPackageJson(obj) {
        this.modalTxt = obj;
        this.modal = true;
      },
      openServices(services) {
        this.listServices = services;
        this.modalServices = true;
      },
      ...frontsStore.mapActions([loadResume]),
    },
    mounted() {
      this.refresh();
    },
  };
</script>
