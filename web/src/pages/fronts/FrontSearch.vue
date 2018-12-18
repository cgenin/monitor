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
              <li>Nombre de résultats : <strong>{{fronts.length}}</strong></li>
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
            <q-btn outline icon="print" @click="print">&nbsp;Imprimer</q-btn>
          </div>

        </div>
      </q-card-main>
    </q-card>
    <q-card>
      <div class="shadow-2 bg-white graph-container">
        <div class="ul-tree  fix " :class="{horizontal:vertical, vertical:!vertical}">
          <ul class="root">
            <li>
              <p class="root">{{resource}}</p>
              <ul>
                <li v-for="u in fronts" :resource="u" :key="u" class="nopointer">
                  <p>
                    {{u}}
                  </p>
                </li>
              </ul>
            </li>
          </ul>
        </div>

      </div>
    </q-card>
  </div>
</template>
<script>
  import { createNamespacedHelpers } from 'vuex';
  import { findByService, fronts, nameModule, reset } from '../../store/fronts/constants';

  const frontsStore = createNamespacedHelpers(nameModule);

  export default {
    name: 'DependenciesSearch',
    data() {
      return { resource: '', vertical: false };
    },
    computed: {
      ...frontsStore.mapGetters([fronts]),
    },
    methods: {
      print() {
        window.print();
      },
      ...frontsStore.mapActions([findByService, reset]),
    },
    mounted() {
      this.resource = this.$router.history.current.params.resource;
      this.reset()
        .then(() => this.findByService(this.resource));
    },

  };
</script>
