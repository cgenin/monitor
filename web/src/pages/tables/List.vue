<template>
  <div class="page-list">
    <q-card>
      <q-card-main>
        <transition
          appear
          enter-active-class="animated fadeIn"
          leave-active-class="animated fadeOut"
        >
          <div v-if="!loading" style="width:100%">
            <div class="sql-table-and-project">
              <q-table
                :data="tables"
                :columns="columns"
                row-key="name"
                :visible-columns="visibleColumns"
                :filter="filter"
                :separator="separator"
                :no-data-label="noData"
                :pagination.sync="pagination"
                :no-results-label="noDataAfterFiltering"
                :rowsPerPageOptions="rowsPerPageOptions"
                @refresh="refresh">
                <template slot="top-left" slot-scope="props">
                  <q-search
                    v-model="filter"
                    class="col-auto"
                  />
                </template>
                <template slot="top-right" slot-scope="props">
                  <q-table-columns
                    class="q-mr-sm"
                    v-model="visibleColumns"
                    :columns="columns"
                  />
                  <q-select
                    color="secondary"
                    v-model="separator"
                    :options="separatorOptions"
                    hide-underline
                  />
                </template>
                <q-td slot="body-cell-services" slot-scope="props" :props="props">
                  <ul>
                    <li v-for="s in props.value" :key="s">{{s}}</li>
                  </ul>
                </q-td>
              </q-table>
            </div>
          </div>
        </transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"/>
        </q-inner-loading>
      </q-card-main>
    </q-card>
  </div>
</template>
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import {loadTables, nameModule, tables,} from '../../store/microservices/constants';
  import {sortString} from '../../FiltersAndSorter';
  import {
    noData,
    noDataAfterFiltering,
    pagination,
    rowsPerPageOptions,
    separator,
    separatorOptions
  } from '../../datatable-utils';
  import {TableDto} from "../../store/microservices/types";

  const microservices = namespace(nameModule);

  @Component
  export default class TablesList extends Vue {
    filter= '';
    loading= false;
    pagination = pagination;
    separator = separator;
    separatorOptions = separatorOptions;
    rowsPerPageOptions = rowsPerPageOptions;
    visibleColumns= ['name', 'services', 'latest'];
    noData = noData;
    noDataAfterFiltering = noDataAfterFiltering;
    columns = [
      {
        label: 'Nom',
        name: 'name',
        field: 'name',
        align: 'left',
        width: '400px',
        sortable: true,
        type: 'string',
        filter: true,
      },
      {
        label: 'Projet(s) lié(s)',
        name: 'services',
        field: 'services',
        align: 'left',
        width: '380px',
        filter: true,
        sort(a: string, b: string) {
          if (a.length === b.length) {
            return sortString(a[0], b[0]);
          }
          return a.length - b.length;
        },
      },
      {
        label: 'Dernière Mise à jour',
        name:
          'latest',
        field:
          'latest',
        align:
          'center',
        width:
          '230px',
        sortable:
          true,
        type:
          'date',
        filter:
          true,
      },
    ];

    @microservices.Getter(tables) tables: TableDto[];
    @microservices.Action(loadTables) loadTables: () => Promise<void>;

    refresh() {
      this.loading = true;
      this.loadTables()
        .then(() => {
          this.filter = '';
          setTimeout(() => {
            this.loading = false;
          }, 300);
        });
    }

    mounted() {
      this.refresh();
    }
  }
</script>
<style lang="stylus" scoped>
  .page-list
    .sql-table-and-project
      .q-table-control
        .q-search
          min-width 30vw
</style>
