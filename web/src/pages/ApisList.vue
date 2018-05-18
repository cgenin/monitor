<template>
  <div class="apis-page page-list container">
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
            <q-item v-for="api in datas" class="list-apis-table" :key="`${api.method}-${api.absolutePath}`">
              <q-item-side>
                <method-icon :method="api.method"></method-icon>
              </q-item-side>
              <q-item-main :label="api.path" :sublabel="api.comment">
              </q-item-main>
            </q-item>
          </q-list>
        </div>
        <div v-if="viewCard">
          <q-infinite-scroll :handler="loadMore">
            <div class="card-container">
              <apis-card :api="api" v-for="api in listCards" :key="`${api.method}-${api.absolutePath}`"></apis-card>
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
  import MethodIcon from '../components/MethodIcon';
  import ApisCard from '../components/ApisCard';
  import filtering, {filteringByAttribute} from '../FiltersAndSorter';
  import EndpointsStore from '../stores/EndpointsStore';
  import HeaderApp from '../components/HeaderApp';

  const sortApis = (a, b) => {
    return a.absolutePath.localeCompare(b.absolutePath);
  };

  const extractQueryParams = (path) => {
    if (!path || path.indexOf('&') === -1) {
      return [];
    }
    return path.replace(/^(.*)\?/, '')
      .split('&');
  };

  const methodFiltering = filteringByAttribute('method');
  const artifactIdFiltering = filteringByAttribute('artifactId');
  const absolutePathFiltering = filteringByAttribute('absolutePath');

  const maxLoadedCard = 20;

  export default {
    name: 'ApisList',
    components: {
      HeaderApp,
      MethodIcon,
      ApisCard
    },
    data() {
      return {
        nb: 25,
        filter: '',
        page: 1,
        datas: [],
        datasAsTree: {},
        datasPrepareAsTree: {},
        original: [],
        listCards: [],
        viewTable: true,
        viewTree: false,
        viewCard: false,
        filtersPanel: false,
        subFilters: {},
        methodsOptions: [
          {label: '', value: null},
          {label: 'GET', value: 'GET'},
          {label: 'POST', value: 'POST'},
          {label: 'PUT', value: 'PUT'},
          {label: 'DELETE', value: 'DELETE'},
          {label: 'HEAD', value: 'HEAD'},
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
          }
        ]
      };
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
        const mF = methodFiltering(this.original, this.subFilters.method);
        const aF = absolutePathFiltering(mF, this.subFilters.path);
        const aiF = artifactIdFiltering(aF, this.subFilters.domain);
        this.datas = filtering(aiF, this.filter);
        this.listCards = this.datas.filter((o, index) => index < maxLoadedCard);
        this.createTree(this.datas);
      },
      loadMore(index, done) {
        if (index < (this.datas.length - 1)) {
          setTimeout(() => {
            if ((index + maxLoadedCard) >= this.datas.length) {
              this.listCards = this.datas;
            }
            else {
              this.listCards = this.datas.filter((o, i) => i < index + maxLoadedCard);
            }
            done();
          }, 600);
        }
        else {
          done();
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
      createTree(data) {
        this.datasPrepareAsTree = {};
        data.forEach((api) => {
          this.datasPrepareAsTree[api.artifactId] = this.datasPrepareAsTree[api.artifactId] || {};
          let previous = this.datasPrepareAsTree[api.artifactId];
          api.path.split('/').forEach((entry) => {
            if (entry) {
              previous[entry] = previous[entry] || {};
              previous = previous[entry];
            }
          });
          previous.method = api.method;
          previous.comment = api.comment;
        });
        let i = 0;
        this.datasAsTree = this.adaptForQTree(this.datasPrepareAsTree, i);
      },
      adaptForQTree(entry = {}, i) {
        return Object.keys(entry).map((k) => {
          if (k === 'method' || k === 'comment') {
            return null;
          }
          i++;
          return {
            id: k + i,
            label: k,
            children: [...this.adaptForQTree(entry[k], i)].filter((el) => el),
            body: 'method',
            method: entry[k].method,
            comment: entry[k].comment,
            header: 'generic'
          };
        })
      }
    },
    mounted() {
      const {nb, page} = this;
      EndpointsStore.find({nb, page})
        .then(list => {
          let l = list.map(o => {
            const context = (o.artifactId || '').replace('-client', '');
            const absolutePath = `/${context}${o.path}`
              .replace(/\?(.*)$/, '');
            const queryParams = extractQueryParams(`/${context}${o.path}`);
            return Object.assign({}, o, {absolutePath, queryParams});
          }).sort(sortApis);
          this.original = l;
          this.datas = l;
          this.createTree(l);
        });
    }

  }
</script>
<style>
  .apis-page .inputs {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
  }

  .apis-page .inputs > div {
    padding-right: 20px;
  }

  .apis-page .inputs > div:last-child {
    padding-right: 0;
  }

  .apis-page .inputs .q-field {
    flex: 5;
    margin: 0;
  }

  .apis-page .inputs-panel {
    overflow: hidden;
    background: #eeeeee;
    padding: 15px;
  }

  .apis-page .inputs-panel > div {
    flex: 1;
    margin: 0 10px;
  }

  .apis-page .q-card-separator {
    margin-bottom: 15px;

  }

  .apis-page .caption {
    font-weight: bold;
  }

  .apis-page .awaiting {
    margin: 1em auto;
    width: 100%;
    display: flex;
    justify-content: center;
  }

  .apis-page .card-container {
    display: flex;
    justify-content: space-around;
    align-items: flex-start;
    flex-flow: row wrap;
    align-content: space-between;
    width: 100%;
  }

  .apis-page .list-apis-table .q-item-label {
    word-break: break-all;
    padding: 5px;
  }

  .apis-page .list-apis-table .q-item-sublabel {
    word-break: break-all;
    padding: 5px;
  }

  .apis-page .card-container .apis-card {
    width: 35vw;
  }


</style>
