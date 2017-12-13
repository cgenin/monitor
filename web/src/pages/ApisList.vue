<template>
  <div class="apis-page page-list">
    <q-card>
      <ul class="breadcrumb">
        <li>
          <router-link to="/">
            <q-icon name="home" />
          </router-link>
        </li>
        <li>
          <router-link to="" active-class="router-link-active">
            <q-icon name="explore" /> Liste des Apis
          </router-link>
        </li>
      </ul>
    </q-card>
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
              float-label="Filtrer"
              @change="filtering" ></q-input>
          </q-field>
          <div>
            <q-toggle v-model="viewTable" label="Affichage table ou card" color="tertiary"></q-toggle>
          </div>
        </div>
        <div class="inputs">
          <q-btn class="btn-flat-primary" @click="showOrHideFilterPanel" flat icon="add" v-bind:class="{ open: filtersPanel }">&nbsp;filtres</q-btn>
        </div>
        <div v-if="filtersPanel" class="inputs inputs-panel">
          <q-select
            float-label="Méthode"
            v-model="subFilters.method"
            :options="methodsOptions"
            @change="filtering"
            class="field-input"></q-select>
          <q-input @change="filtering" type="text" float-label="Path" v-model="subFilters.path"
                   class="field-input"></q-input>
          <q-input @change="filtering" type="text" float-label="Domaine" v-model="subFilters.domain"
                   class="field-input"></q-input>
        </div>
        <q-card-separator></q-card-separator>
        <div class="results-number">
          <p class="caption">Résultat : {{datas.length}}</p>
        </div>
        <div v-if="viewTable">
          <table class="table-result q-table vertical-separator bordered striped-odd highlight responsive ">
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
      </q-card-main>
    </q-card>
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
    QToggle,
    QInfiniteScroll,
    QSpinnerDots,
    QIcon,
    QField
  } from 'quasar';
  import MethodIcon from '../components/MethodIcon';
  import ApisCard from '../components/ApisCard';
  import filtering, {filteringByAttribute} from '../Filters';
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
      QToggle,
      QInfiniteScroll,
      QSpinnerDots,
      MethodIcon,
      ApisCard,
      QIcon,
      QField
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
<style lang="stylus" scoped>
  .apis-page
    .inputs
      display flex
      justify-content space-between
      align-items center
      margin-bottom 10px
      > div
        padding-right 20px
        &:last-child
          padding-right 0
      .q-field
        flex 5
        margin 0
    .inputs-panel
      background #eeeeee
      padding 15px
      > div
        flex 1
        margin 0 10px
    .q-card-separator
      margin-bottom 15px
    .caption
      font-weight bold
    .awaiting
      margin 1em auto
      width 100%
      display flex
      justify-content center
    .card-container
      display flex
      justify-content space-around
      align-items flex-start
      flex-flow row wrap
      align-content space-between
      width 100%
      .apis-card
        width 35vw
  .api-url
    word-break break-all
    width 35vw
  .api-comment
    max-width: $api-max-comments-width;
    min-width: $api-max-comments-width;
    width: $api-max-comments-width;
</style>
