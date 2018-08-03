<template>
  <div class="apis-page container">
    <header-app :bc-datas="[{icon:'explore', label:'Liste des Apis'}]"/>

    <q-card>
      <q-card-title>
        <h3>Liste des apis</h3>
      </q-card-title>
      <q-card-separator/>
      <q-card-main>
        <div class="inputs">
          <q-field
            icon="search">
            <q-input
              v-model="filter"
              type="text"
              class="filter"
              :autofocus="true"
              float-label="Filtrer"
              @input="filtering"></q-input>
          </q-field>
          <div class="column">
            <label>Affichage :</label>
            <q-toggle @input="changedTable" v-model="viewTable" label="table" color="tertiary"></q-toggle>
            <q-toggle @input="changedCard" v-model="viewCard" label="carte" color="tertiary"></q-toggle>
            <q-toggle @input="changedTree" v-model="viewTree" label="arbre" color="tertiary"></q-toggle>
          </div>
        </div>
        <div class="inputs">
          <q-btn class="btn-flat-primary" @click="showOrHideFilterPanel" flat icon="add"
                 v-bind:class="{ open: filtersPanel }">&nbsp;filtres
          </q-btn>
        </div>
        <div v-if="filtersPanel" class="inputs inputs-panel" v-bind:class="{ open: filtersPanel }">
          <q-select
            float-label="Méthode"
            v-model="subFilters.method"
            :options="methodsOptions"
            @input="filtering"
            class="field-input"></q-select>
          <q-input @input="filtering" type="text" float-label="Path" v-model="subFilters.path"
                   class="field-input"></q-input>
          <q-input @input="filtering" type="text" float-label="Domaine" v-model="subFilters.domain"
                   class="field-input"></q-input>
        </div>
        <q-card-separator></q-card-separator>
        <div class="results-number">
          <p class="caption">Résultat : {{datas.length}}</p>
        </div>
        <div v-if="viewTable">
          <q-list>
            <q-item v-for="api in datas" class="list-apis-table" :key="`table-${api.method}-${api.absolutePath}`">
              <q-item-side>
                <method-icon :method="api.method"></method-icon>
              </q-item-side>
              <q-item-main :label="api.path" :sublabel="api.comment">
              </q-item-main>
            </q-item>
          </q-list>
        </div>
        <div v-if="viewCard">
          <q-infinite-scroll :handler="loadMore" ref="infiniteScroll">
            <div class="card-container">
              <apis-card :api="api" v-for="api in listCards"
                         :key="`card-${api.method}-${api.absolutePath}`"></apis-card>
            </div>
            <div class="awaiting" slot="message">
              <q-spinner-dots color="red" :size="40"></q-spinner-dots>
            </div>
          </q-infinite-scroll>
        </div>
        <div v-if="viewTree">
          <q-tree
            :nodes="datasAsTree"
            node-key="id"
          >
            <div slot="header-generic" slot-scope="prop" class="row items-center">
              <div class="text-weight-bold text-primary">{{ prop.node.label }}&nbsp;&nbsp;</div>
              <method-icon :method="prop.node.method" v-if="prop.node.method"></method-icon>
              <q-item-side icon="help" v-if="prop.node.comment">
                <q-tooltip>
                  {{prop.node.comment}}
                </q-tooltip>
              </q-item-side>
            </div>

          </q-tree>
        </div>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import {
    namespace as namespaceMicroService,
    apis,
    loadApis,
  } from '../store/microservices/constants';
  import filtering, { filteringByAttribute } from '../FiltersAndSorter';
  import MethodIcon from '../components/MethodIcon';
  import ApisCard from '../components/ApisCard';
  import HeaderApp from '../components/HeaderApp';

  const microServicesStore = createNamespacedHelpers(namespaceMicroService);
  const methodFiltering = filteringByAttribute('method');
  const artifactIdFiltering = filteringByAttribute('artifactId');
  const absolutePathFiltering = filteringByAttribute('absolutePath');

  const maxLoadedCard = 20;

  const adaptForQTree = (entry = {}, i) => Object
    .keys(entry)
    .map((k, index) => {
      if (k === 'method' || k === 'comment') {
        return null;
      }
      const nb = i + index;
      return {
        id: k + nb,
        label: k,
        children: [...adaptForQTree(entry[k], nb)].filter(el => el),
        body: 'method',
        method: entry[k].method,
        comment: entry[k].comment,
        header: 'generic',
      };
    });

  const createTree = (data) => {
    const datasPrepareAsTree = {};
    data.forEach((api) => {
      datasPrepareAsTree[api.artifactId] = datasPrepareAsTree[api.artifactId] || {};
      let previous = datasPrepareAsTree[api.artifactId];
      api.path.split('/').forEach((entry) => {
        if (entry) {
          previous[entry] = previous[entry] || {};
          previous = previous[entry];
        }
      });
      previous.method = api.method;
      previous.comment = api.comment;
    });
    const i = 0;
    return adaptForQTree(datasPrepareAsTree, i);
  };

  export default {
    name: 'ApisList',
    components: {
      HeaderApp,
      MethodIcon,
      ApisCard,
    },
    data() {
      return {
        nb: 25,
        filter: '',
        page: 1,
        datas: [],
        datasAsTree: {},
        listCards: [],
        viewTable: true,
        viewTree: false,
        viewCard: false,
        filtersPanel: false,
        subFilters: {},
        methodsOptions: [
          { label: '', value: null },
          { label: 'GET', value: 'GET' },
          { label: 'POST', value: 'POST' },
          { label: 'PUT', value: 'PUT' },
          { label: 'DELETE', value: 'DELETE' },
          { label: 'HEAD', value: 'HEAD' },
        ],
        columns: [
          {
            label: 'Méthode',
            name: 'method',
            field: 'nmethod',
            align: 'left',
            sortable: true,
          },
          {
            label: 'Path',
            name: 'path',
            field: 'path',
            align: 'left',
            sortable: true,
          },
          {
            label: 'Commentaire',
            name: 'comment',
            field: 'comment',
            align: 'left',
            sortable: true,
          },
        ],
      };
    },
    computed: {
      ...microServicesStore.mapGetters([apis]),
    },
    methods: {
      showOrHideFilterPanel() {
        this.filtersPanel = !this.filtersPanel;
        if (!this.filtersPanel) {
          this.subFilters = {};
          this.filtering();
        }
      },
      filtering() {
        const mF = methodFiltering(this.apis, this.subFilters.method);
        const aF = absolutePathFiltering(mF, this.subFilters.path);
        const aiF = artifactIdFiltering(aF, this.subFilters.domain);
        this.datas = filtering(aiF, this.filter);
        this.listCards = this.datas.filter((o, index) => index < maxLoadedCard);
        this.datasAsTree = createTree(this.datas);
      },
      loadMore(index, done) {
        if (index < (this.datas.length - 1)) {
          if ((index + maxLoadedCard) >= this.datas.length) {
            this.listCards = this.datas;
          } else {
            this.listCards = this.datas
              .filter((o, i) => i < index + maxLoadedCard);
          }
          done();
        } else {
          if (this.listCards.length !== this.datas.length) {
            this.listCards = this.datas;
          }
          done(true);
          this.$refs.infiniteScroll.stop();
        }
      },
      changedTable(v) {
        if (!v) {
          this.viewTable = true;
        }
        this.viewTree = false;
        this.viewCard = false;
      },
      changedCard(v) {
        if (!v) {
          this.viewCard = true;
        }
        this.viewTable = false;
        this.viewTree = false;
      },
      changedTree(v) {
        if (!v) {
          this.viewTree = true;
        }
        this.viewTable = false;
        this.viewCard = false;
      },
      ...microServicesStore.mapActions([loadApis]),
    },
    mounted() {
      const { nb, page } = this;
      this.loadApis({ nb, page })
        .then(() => {
          this.datas = this.apis;
          this.datasAsTree = createTree(this.apis);
        });
    },

  };
</script>
<style lang="stylus">
  @import "../css/pages/apilist.styl"
</style>
