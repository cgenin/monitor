<template>
  <div class="projects-page">
    <q-card>
      <q-card-title>
        <h4>Liste des Projets</h4>
      </q-card-title>

      <q-card-separator/>
      <q-card-main>
        <div class="inputs">
          <q-input v-model="filter" type="text" class="filter" float-label="filter" @change="filtering"></q-input>
          <q-btn round color="primary" icon="refresh" @click="refresh"></q-btn>
        </div>
        <q-card-separator/>
        <q-transition
          appear
          enter="fadeIn"
          leave="fadeOut"
        >
          <div v-show="!loading">
            <div class="font-result results-number">
              <strong>Résultats : {{list.length}}</strong>
            </div>
            <table class="font-result q-table highlight responsive results">
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
                <td>
                  <router-link :to="project.destinationUrl">{{project.name}}</router-link>
                </td>
                <td>
                  <router-link :to="project.destinationUrl">{{project.snapshot}}</router-link>
                </td>
                <td>
                  <router-link :to="project.destinationUrl">{{project.release}}</router-link>
                </td>
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
          </div>
        </q-transition>
        <q-inner-loading :visible="loading">
          <q-spinner-gears size="50px" color="primary"></q-spinner-gears>
        </q-inner-loading>
      </q-card-main>
    </q-card>
    <q-modal v-model="modal" :content-css="{minWidth: '80vw'}">
      <div>
        <h4 slot="header" class="header-modal-deps">{{modalOpt.title}}</h4>
        <div slot="content">
          <q-list highlight>
            <q-item v-for="d in modalOpt.data" key="d">
              <q-item-main class="text-secondary">{{d}}</q-item-main>
            </q-item>
          </q-list>
        </div>
        <q-btn slot="footer" class="btn-close-modal" color="primary" @click="modal = false">Fermer</q-btn>
      </div>
    </q-modal>
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
    QTooltip,
    QModal,
    QList,
    QItem,
    QItemMain,
    QInnerLoading,
    QTransition,
    QSpinnerGears
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
      ChangelogButton,
      QInnerLoading,
      QTransition,
      QSpinnerGears
    },
    data() {
      return {list: [], original: [], filter: '', modal: false, modalOpt: {}, loading: false};
    },
    methods: {
      refresh() {
        this.loading = true;
        ProjectsStore.initialize()
          .then((list) => {
            const res = map(list);
            this.list = res;
            this.original = res;
            this.filter = '';
            this.loading = false;
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
  .projects-page table.results {
    width: 100%;
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
