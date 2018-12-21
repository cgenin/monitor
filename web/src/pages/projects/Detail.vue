<template>
  <div class="main container">
    <header-app :help="txtHelp"
                :bc-datas="[{icon:'view_list', label:'Liste des Projets', path : ProjectsList},{icon:'fas fa-file-alt', label:title}]"></header-app>
    <q-card>
      <q-card-title>
        <h3>{{title}}</h3>
      </q-card-title>
      <q-card-main>
        <p class="caption">Sélectionner une version</p>
        <q-select v-model="id" :options="selectVersions" @change="changeVersion">
        </q-select>
      </q-card-main>
    </q-card>
    <div class="selected-list">
      <div>
      </div>
      <q-list separator class="bg-white">
        <q-collapsible icon="fas fa-coffee" label="Librairies Java" v-if="selected.javaDeps"
                       :disable="selected.javaDeps.length === 0"
                       :sublabel="`Nombre : ${selected.javaDeps.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.javaDeps" :key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="border_all" label="Tables" v-if="selected.tables"
                       :disable="selected.tables.length === 0"
                       :sublabel="`Nombre : ${selected.tables.length}`">
          <div class="list-table">
            <ul>
              <li v-for="deps in selected.tables" :key="deps">{{deps}}</li>
            </ul>
          </div>
        </q-collapsible>
        <q-collapsible icon="explore" label="Apis" v-if="selected.apis"
                       :disable="selected.apis.length === 0"
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
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import {namespace} from 'vuex-class';
  import VueMarkdown from 'vue-markdown';
  import {ProjectsList} from '../../Routes';
  import {getProject, getVersionsForProject, nameModule, project, versions,} from '../../store/microservices/constants';
  import HeaderApp from '../../components/HeaderApp';
  import {sortStringForSorter} from '../../FiltersAndSorter';
  import {Project, VersionDto} from '../../store/microservices/types';

  const txtHelp = require('./help.md');

  const microServices = namespace(nameModule);

  @Component({
    components: {
      HeaderApp,
      VueMarkdown,
    },
  })
  export default class ProjectDetail extends Vue {
    id = null;
    latest: VersionDto;
    txtHelp = txtHelp;
    selected: VersionDto;
    selectVersions = [];
    title = '';
    ProjectsList = ProjectsList;
    @microServices.Getter(project) project: Project;
    @microServices.Getter(versions) versions: VersionDto[];
    @microServices.Action(getProject) getProject: (id: string) => Promise<void>;
    @microServices.Action(getVersionsForProject) getVersionsForProject: (id: string) => Promise<void>;

    changeVersion(id) {
      this.selected = this.versions.find(v => v.id === id);
    }

    mounted() {
      const {id} = this.$route.params;
      Promise.all([this.getProject(id), this.getVersionsForProject(id)])
        .then(() => {
          this.title = `Détail du projet : ${this.project.name}`;
          this.latest = this.versions
            .map((v) => {
              v.tables = v.tables.sort();
              v.javaDeps = v.javaDeps.sort();
              v.apis = v.apis.sort();
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
            .map((v) => {
              const stamp = (v.isSnapshot) ? 'snapshot' : 'release';
              return {
                label: v.name,
                value: v.id,
                stamp,
              };
            })
            .sort(sortStringForSorter(a => a.label));
        })
        .catch(err => console.error(err));
    }
  }
</script>
<style lang="stylus" scoped>
  .main
    background inherit

  .selected-list
    padding 10px 0
    background none
</style>
