<template>
  <div class="dependencies-search-page">
    <q-card>
        <q-card-main>
          <div class="row justify-around">
            <div>
              <ul>
                <li>Ressource : <strong>{{resource}}</strong></li>
              </ul>
            </div>
            <div>
              <ul>
                <li>Nombre de résultats : <strong>{{usedBy.length}}</strong></li>
              </ul>
            </div>
            <div>
              <ul class="no-bullet">
                <li>
                  Style :
                  <q-radio v-model="vertical" :val="true" label="Vertical"></q-radio>
                  <q-radio v-model="vertical" :val="false" label="Horizontal"></q-radio>
                </li>
              </ul>
            </div>
            <div class="print-button">
              <q-btn outline icon="fa-print" @click="print">Imprimer</q-btn>
            </div>

          </div>
        </q-card-main>
      </q-card>
    <q-card>
        <div class="shadow-2 bg-white graph-container">
          <div class="ul-tree  fix " :class="{horizontal:vertical, vertical:!vertical}">
            <ul class="root">
              <li >
                <p class="root">{{resource}}</p>
                <ul>
                  <sub-tree v-for="u in usedBy" :resource="u" :key="u"></sub-tree>
                </ul>
              </li>
            </ul>
          </div>

        </div>
      </q-card>
  </div>
</template>
<script>
  import DependenciesStore from '../../stores/DependenciesStore';
  import SubTree from '../../components/SubTree';

  export default {
    name: 'DependenciesSearch',
    components: {
      SubTree,
    },
    data() {
      return {resource: '', usedBy: [], vertical: false};
    },
    methods: {
      print() {
        window.print();
      }
    },
    mounted() {
      this.resource = this.$router.history.current.params.resource;
      DependenciesStore.resetDependencies();
      DependenciesStore.usedBy(this.resource)
        .then(usedBy => {
          this.usedBy = usedBy;
        })
    }

  }
</script>
