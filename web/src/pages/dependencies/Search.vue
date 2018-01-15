<template>
  <div class="dependencies-search-page">
    <div style="display: flex;flex-direction: column">
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
      <div class="row justify-center">
        <div class="shadow-2 bg-white graph-container">
          <div class="ul-tree  fix " :class="{horizontal:vertical, vertical:!vertical}">
            <ul>
              <li>
                <p>{{resource}}</p>
                <ul>
                  <sub-tree v-for="u in usedBy" :resource="u" :key="u"></sub-tree>
                </ul>
              </li>
            </ul>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>
<script>
  import {
    QCard,
    QCardMain,
    QRadio,
    QBtn
  } from 'quasar';
  import DependenciesStore from '../../stores/DependenciesStore';
  import SubTree from '../../components/SubTree';

  export default {
    name: 'DependenciesSearch',
    components: {
      SubTree,
      QCard,
      QCardMain,
      QRadio,
      QBtn
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

<style>

  .dependencies-search-page ul.no-bullet {
    list-style-type: none;
  }

  .dependencies-search-page .print-button {
    margin-left: 1em;
    margin-top: .5em;
  }

  .dependencies-search-page .graph-container {
    padding: 10px;
    display: flex;
    justify-content: center;
    min-width: 98%;
  }

  .dependencies-search-page .nopointer {
    cursor: not-allowed;
  }

  .dependencies-search-page .withpointer {
    cursor: pointer;
  }

  .ul-tree.horizontal ul {
    position: relative;
    margin: 0;
    padding: 0 0 0 1em;
    list-style: none;
  }

  .ul-tree.horizontal ul:before {
    position: absolute;
    content: '';
    top: 50%;
    left: 0;
    width: 1em;
    border-top: 1px solid #999;
  }

  .ul-tree.horizontal li {
    position: relative;
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 0 0 0 1em;
  }

  .ul-tree.horizontal li:before {
    position: absolute;
    content: '';
    top: 0;
    left: 0;
    bottom: 0;
    border-left: 1px solid #999;
  }

  .ul-tree.horizontal li:first-child:before {
    top: 50%;
  }

  .ul-tree.horizontal li:last-child:before {
    bottom: 50%;
  }

  .ul-tree.horizontal li p {
    display: inline-block;
    position: relative;
    background-color: #eee;
    border: 1px solid #999;
    margin: 1em 0;
    padding: 0.5em;
    text-align: center;
  }

  .ul-tree.horizontal li p:before {
    position: absolute;
    content: '';
    top: 50%;
    left: -1em;
    width: 1em;
    border-top: 1px solid #999;
  }

  .ul-tree.horizontal > ul {
    padding: 0;
  }

  .ul-tree.horizontal > ul:before {
    display: none;
  }

  .ul-tree.horizontal > ul > li:before {
    display: none;
  }

  .ul-tree.horizontal > ul > li > p:before {
    display: none;
  }

  .ul-tree.horizontal.fix p {
    min-width: 7em;
    display: block;
  }

  .ul-tree.vertical ul {
    position: relative;
    margin: 0;

    padding: 1em 0 0 0;
    list-style: none;
    display: flex;
    flex-direction: row;
  }

  .ul-tree.vertical ul:before {
    position: absolute;
    content: '';
    left: 50%;
    top: 0;
    height: 1em;
    border-left: 1px solid #999;
  }

  .ul-tree.vertical li {
    display: inline-block;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 1em 0 0 0;
  }

  .ul-tree.vertical li:before {
    position: absolute;
    content: '';
    top: 0;
    left: 0;
    right: 0;
    border-top: 1px solid #999;
  }

  .ul-tree.vertical li:first-child:before {
    left: 50%;
  }

  .ul-tree.vertical li:last-child:before {
    right: 50%;
  }

  .ul-tree.vertical li p {
    display: inline-block;
    position: relative;
    background-color: #eee;
    border: 1px solid #999;
    margin: 0 1em;
    padding: 0.5em;
    text-align: center;
  }

  .ul-tree.vertical li p:before {
    position: absolute;
    content: '';
    left: 50%;
    top: -1em;
    height: 1em;
    border-left: 1px solid #999;
  }

  .ul-tree.vertical > ul {
    padding: 0;
  }

  .ul-tree.vertical > ul:before {
    display: none;
  }

  .ul-tree.vertical > ul > li:before {
    display: none;
  }

  .ul-tree.vertical > ul > li > p:before {
    display: none;
  }

  .ul-tree.vertical.fix p {
    min-width: 6em;
    min-height: 2.5em;
  }

  @media print {
    ul.no-bullet,
    .dependencies-search-page .print-button {
      display: none;
    }

    .dependencies-search-page .q-card {
      box-shadow: none;
    }

    .dependencies-search-page .shadow-2 {
      box-shadow: none;
    }

  }

</style>
