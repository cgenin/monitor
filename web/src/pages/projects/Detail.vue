<template>
  <div class="main">
    <q-card>
      <q-card-title>
        <h4>{{title}}</h4>
      </q-card-title>
      <q-card-main>
        <p class="caption">Sélectionner une version</p>
        <q-select v-model="id" :options="selectVersions">
        </q-select>
      </q-card-main>
    </q-card>
    <div class="selected-list">
      <q-list>
        <q-collapsible icon="build" label="Librairies Java" :sublabel="`Nombre : ${selected.javaDeps.length}`">
          <div>
            <ul class="text-secondary">
              <li v-for="deps in selected.javaDeps" key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="border_all" label="Tables" :sublabel="`Nombre : ${selected.tables.length}`">
          <div>
            <ul class="text-secondary">
              <li v-for="deps in selected.tables" key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="explore" label="Apis" :sublabel="`Nombre : ${selected.apis.length}`">
          <div>
            <ul class="text-secondary">
              <li v-for="deps in selected.apis" key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="change_history" label="Change Log" >
          <vue-markdown>
            {{selected.changelog}}
          </vue-markdown>
        </q-collapsible>
      </q-list>
    </div>
  </div>
</template>
<script>
  import {
    QCard, QCardTitle, QCardSeparator, QCardMain, QSelect, QBtn, QCollapsible, QList, QItem, QItemMain
  } from 'quasar';
  import VueMarkdown from 'vue-markdown'
  import ProjectsStore from '../../stores/ProjectsStore';
  import {formatYYYYMMDDHHmm} from '../../Dates';

  export default {
    name: 'ProjectDetail',
    components: {
      QCard, QCardTitle, QCardSeparator, QCardMain, QSelect, QBtn, QCollapsible, QList, QItem, QItemMain, VueMarkdown
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
      changeVersion(evt) {
        const id = evt.target.value;
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
          console.log(this.selected)
          this.selectVersions = this.versions
            .map(v => {
              const stamp = (v.isSnapshot) ? 'snapshot' : 'release';
              return {
                label: v.name,
                value: v.id,
                stamp
              }
            });
        })
        .catch(err => console.error(err));
    }
  };
</script>
<style scoped>
  .main{
    background: inherit;
  }
  .selected-list {
    padding: 10px;
    background: none;
  }
</style>
