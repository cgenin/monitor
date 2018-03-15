<template>
  <div class="main">
    <bread-crumb
      :datas="[{icon:'view_list', label:'Liste des Projets', path : '/projects-list'},{icon:'ion-document-text', label:title}]"/>
    <q-card>
      <q-card-title>
        <h3>{{title}}</h3>
      </q-card-title>
      <q-card-main>
        <p class="caption">Sélectionner une version</p>
        <q-select v-model="id" :options="selectVersions"  @change="changeVersion">
        </q-select>
      </q-card-main>
    </q-card>
    <div class="selected-list">
      <div>
      </div>
      <q-list separator class="bg-white">
        <q-collapsible icon="ion-coffee" label="Librairies Java" v-if="selected.javaDeps"
                       :disable="selected.javaDeps.length === 0" :sublabel="`Nombre : ${selected.javaDeps.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.javaDeps" :key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="border_all" label="Tables" v-if="selected.tables" :disable="selected.tables.length === 0"
                       :sublabel="`Nombre : ${selected.tables.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.tables" :key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="explore" label="Apis" v-if="selected.apis" :disable="selected.apis.length === 0"
                       :sublabel="`Nombre : ${selected.apis.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.apis" :key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="change_history" label="Change Log">
          <vue-markdown :source="selected.changelog">
          </vue-markdown>
        </q-collapsible>
      </q-list>
    </div>
  </div>
</template>
<script>
  import {
    QCard,
    QCardTitle,
    QCardSeparator,
    QCardMain,
    QSelect,
    QBtn,
    QCollapsible,
    QList,
    QItem,
    QItemMain,
    QIcon
  } from 'quasar';
  import BreadCrumb from '../../components/BreadCrumb'
  import VueMarkdown from 'vue-markdown'
  import ProjectsStore from '../../stores/ProjectsStore';
  import {formatYYYYMMDDHHmm} from '../../Dates';
  import {sortString} from '../../FiltersAndSorter'

  export default {
    name: 'ProjectDetail',
    components: {
      BreadCrumb,
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QSelect,
      QBtn,
      QCollapsible,
      QList,
      QItem,
      QItemMain,
      VueMarkdown,
      QIcon
    },
    data() {
      return {
        id: null,
        project: {},
        selected: {},
        versions: [],
        selectVersions: [],
        title: '',
      }
    },
    methods: {
      changeVersion(id) {
        this.selected = this.versions.find(v => v.id === id);
      }
    },
    mounted() {
      let id = this.$route.params.id;
      Promise.all([ProjectsStore.getProject(id), ProjectsStore.getVersion(id)])
        .then(() => {
          this.project = ProjectsStore.project;
          this.title = `Détail du projet : ${this.project.name}`;
          this.versions = ProjectsStore.versions
            .map(o => {
              const latest = formatYYYYMMDDHHmm(o.latestUpdate);
              return Object.assign({}, o, {latest});
            });
          this.latest = this.versions
            .map(v => {
              v.table = v.tables.sort();
              v.javaDeps = v.javaDeps.sort();
              v.apis = v.apis.sort();
              v.icon = (v.isSnapshot) ? '/img/snapshot.svg' : '/img/release.svg';
              return v;
            })
            .reduce((a, b) => {
              if (a.latestUpdate > b.latestUpdate) {
                return a;
              }
              return b;
            });
          this.id = this.latest.id;
          this.selected = this.latest;
          this.selectVersions = this.versions
            .map(v => {
              const stamp = (v.isSnapshot) ? 'snapshot' : 'release';
              return {
                label: v.name,
                value: v.id,
                stamp
              }
            })
            .sort(sortString((a) => a.label));
          console.log(this.selectVersions)
        })
        .catch(err => console.error(err));
    }
  };
</script>
<style scoped>
  .main {
    background: inherit;
  }

  .selected-list {
    padding: 10px;
    background: none;
  }
</style>
