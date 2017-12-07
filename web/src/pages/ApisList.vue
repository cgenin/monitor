<template>
  <div class="apis-page">
    <q-card>
      <q-card-title>
        <h4>Liste des apis</h4>
      </q-card-title>
      <q-card-main>
        <div class="inputs">
          <q-input v-model="filter" type="text" class="filter" float-label="filter"
                   @change="filtering"></q-input>
          <div>
            <q-radio v-model="viewTable" label="Table" color="teal" :val="true"></q-radio>
            <q-radio v-model="viewTable" label="Card" color="orange" :val="false"></q-radio>
          </div>
          <q-btn @click="showOrHideFilterPanel" v-if="!filtersPanel" flat icon="add">&nbsp;filtres</q-btn>
          <q-btn @click="showOrHideFilterPanel" v-if="filtersPanel" flat icon="close">&nbsp;filtres</q-btn>
        </div>
        <div v-if="filtersPanel" class="inputs">
          <q-select float-label="Méthode" v-model="subFilters.method" :options="methodsOptions"
                    @change="filtering" class="field-input"></q-select>
          <q-input @change="filtering" type="text" float-label="Path" v-model="subFilters.path"
                   class="field-input"></q-input>
          <q-input @change="filtering" type="text" float-label="Domaine" v-model="subFilters.domain"
                   class="field-input"></q-input>
        </div>
        <q-card-separator></q-card-separator>
        <div v-if="viewTable">

          <p class="caption">Résultat : {{datas.length}}</p>
          <table class="table-result q-table highlight responsive ">
            <thead>
            <tr>
              <th>Méthode</th>
              <th>path</th>
              <th>Commentaire</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="api in datas">
              <td>
                <method-icon :method="api.method"></method-icon>
              </td>
              <td class="api-url">
                {{api.absolutePath}}
              </td>
              <td class="api-comment text-grey-7">
                {{api.comment}}
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </q-card-main>
    </q-card>
    <div v-if="!viewTable">
      <q-infinite-scroll :handler="loadMore">
        <div class="card-container">
          <apis-card :api="api" v-for="api in listCards" key="api.absolutePath"></apis-card>
        </div>
        <div class="awaiting" slot="message">
          <q-spinner-dots color="red" :size="40"></q-spinner-dots>
        </div>
      </q-infinite-scroll>
    </div>
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
    QSelect,
    QModal,
    QList,
    QItem,
    QItemMain,
    QRadio,
    QInfiniteScroll,
    QSpinnerDots
  } from 'quasar';
  import MethodIcon from '../components/MethodIcon'
  import ApisCard from '../components/ApisCard'
  import filtering, {filteringByAttribute} from '../Filters'
  import EndpointsStore from '../stores/EndpointsStore';

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
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInput,
      QBtn,
      QSelect,
      QModal,
      QList,
      QItem,
      QItemMain,
      QRadio,
      QInfiniteScroll,
      QSpinnerDots,
      MethodIcon,
      ApisCard
    },
    data() {
      return {
        nb: 25,
        filter: '',
        page: 1,
        datas: [],
        original: [],
        listCards: [],
        viewTable: true,
        filtersPanel: false,
        subFilters: {},
        methodsOptions: [
          {label: '', value: null},
          {label: 'GET', value: 'GET'},
          {label: 'POST', value: 'POST'},
          {label: 'PUT', value: 'PUT'},
          {label: 'DELETE', value: 'DELETE'},
          {label: 'HEAD', value: 'HEAD'},
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
        });
    }

  }
</script>
<style scoped>
  :root {
    --api-max-comment-width: 200px;
  }

  .apis-page .inputs {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .apis-page .inputs .filter {
    width: 60%;
  }

  .apis-page .field-input {
    min-width: 20%;
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
    align-items: center;
    flex-wrap: wrap;
    width: 100%;
  }

  .api-url {
    word-break: break-all;
    width: 35vw;
  }

  .api-comment {
    max-width: var(--api-max-comment-width);
    min-width: var(--api-max-comment-width);
    width: var(--api-max-comment-width);
  }

</style>
