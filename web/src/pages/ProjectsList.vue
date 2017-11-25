<template>
  <div class="projects-page">
    <q-card>
      <q-card-title>
        Liste des Projets
      </q-card-title>

      <q-card-separator/>
      <q-card-main>
        <div class="inputs">
          <q-input v-model="filter" type="text" class="filter" float-label="filter" @change="filtering"></q-input>
          <q-btn round color="primary" icon="refresh" @click="refresh"></q-btn>
        </div>
        <q-card-separator/>
        <div class="font-result results-number">
          <strong>Résultats : {{list.length}}</strong>
        </div>
        <table class="font-result q-table highlight responsive">
          <thead>
          <tr>
            <th>Nom</th>
            <th>Snapshot</th>
            <th>Release</th>
            <th>Java</th>
            <th>Apis</th>
            <th>Table</th>
            <th>Dernière Mise à jour</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="project in list">
            <td><a class="link">{{project.name}}</a></td>
            <td><a class="link">{{project.snapshot}}</a></td>
            <td><a class="link">{{project.release}}</a></td>
            <td>
              <a href="#" v-on:click.prevent="openJava(project)" class="tootip">
                <span>{{project.javaDeps.length}}&nbsp;</span>
                <i class="material-icons">info </i>
              </a>
            </td>
            <td>
              <a href="#" v-on:click.prevent="openApis(project)" class="tootip">
                <span>{{project.apis.length}}&nbsp;</span>
                <i class="material-icons">info </i>
              </a></td>
            <td><a href="#" v-on:click.prevent="openTables(project)" class="tootip">
              <span>{{project.tables.length}}&nbsp;</span>
              <i class="material-icons">info </i>
            </a></td>
            <td>{{project.latest}}</td>
            <td>
              <changelog-button :content="project.changelog"></changelog-button>
            </td>
          </tr>
          </tbody>
        </table>
      </q-card-main>
    </q-card>
    <q-modal v-model="modal" :content-css="{minWidth: '80vw'}">
      <q-modal-layout
        header-style="min-height: 100px"
        content-class="{'bg-primary': true, 'center': true}"
        footer-style="{fontSize: '24px', fontWeight: 'bold'}"
      >
        <h4 slot="header" class="header-modal-deps">{{modalOpt.title}}</h4>
        <div slot="content">
          <q-list highlight>
            <q-item v-for="d in modalOpt.data" key="d">
              <q-item-main class="text-secondary">{{d}}</q-item-main>
            </q-item>
          </q-list>
        </div>
        <q-btn slot="footer" class="btn-close-modal" color="primary" @click="modal = false">Fermer</q-btn>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import {
    QCard, QCardTitle, QCardSeparator, QCardMain, QInput, QBtn, QTooltip, QModal, QList, QItem, QItemMain
  } from 'quasar';
  import ChangelogButton from '../components/ChangeLogButton'
  import ProjectsStore from '../stores/ProjectsStore';
  import {formatYYYYMMDDHHmm} from '../Dates';
  import filtering from '../Filters'

  const depTootip = (attr) => {
    if (!attr || attr.length === 0) {
      return '<p>Aucune dépendance.</p>';
    }
    return attr.map(v => `<ul><li>${v}</li></ul>`).reduce((a, b) => a + b, '');
  };
  const map = (l) => {
    if (!l) {
      return [];
    }
    return l.map(p => {
      p.destinationUrl = `/projects/${p.id}`;
      p.snapshot = p.snapshot || '-';
      p.release = p.release || '-';
      p.javaDeps = p.javaDeps || [];
      p.npmDeps = p.npmDeps || [];
      p.tables = p.tables || [];
      p.npmDepsTootip = depTootip(p.npmDeps);
      p.latest = formatYYYYMMDDHHmm(p.latestUpdate);
      return p;
    });
  };

  export default {
    name: 'ProjectsList',
    components: {
      QCard,
      QCardTitle,
      QCardSeparator,
      QCardMain,
      QInput,
      QBtn,
      QTooltip,
      QModal,
      QList,
      QItem,
      QItemMain,
      ChangelogButton
    },
    data() {
      return {list: [], original: [], filter: '', modal: false, modalOpt: {}};
    },
    methods: {
      refresh() {
        ProjectsStore.initialize()
          .then((list) => {
            const res = map(list);
            this.list = res;
            this.original = res;
            this.filter = '';
          });
      },
      openJava(p) {
        const data = p.javaDeps.sort();
        const title = 'Dépendance Java';
        this.modalOpt = {
          data, title
        };
        this.modal = true;
      },
      openTables(p) {
        const data = p.tables.sort();
        const title = 'Tables';
        this.modalOpt = {
          data, title
        };
        this.modal = true;
      },

      openApis(p) {
        const data = p.apis.sort();
        const title = 'Apis';
        this.modalOpt = {
          data, title
        };
        this.modal = true;
      },
      filtering() {
        console.log(this.filter)
        this.list = filtering(this.original, this.filter);
      }
    },
    mounted() {
      this.refresh();
    }
  }
</script>
<style scoped>
  .projects-page {

  }

  .projects-page .font-result {
    font-size: 1.3rem;
    line-height: inherit;
  }

  .projects-page .results-number {
    margin-top: .5em;
  }

  .projects-page .inputs {
    display: flex;
    justify-content: space-around;
    align-items: center;
  }

  .projects-page .inputs .filter {
    width: 50%;
  }

  h4.header-modal-deps {
    text-align: center;
  }

  .btn-close-modal {
    width: 100%;
  }
</style>
