<template>
  <q-card class="apis-card">
    <q-card-title>
      <div class="title">
        <div>
          <method-icon class="icon-title" :method="api.method"></method-icon>
        </div>
        <div class="text ">&nbsp;{{api.absolutePath}}</div>
      </div>
    </q-card-title>
    <q-card-main>
      <div>
        <p class="text-grey-7">{{api.comment}}</p>
      </div>

      <p>since: <strong>{{api.since}}</strong></p>

      <q-list no-border separator>
        <q-collapsible label="Projet" :sublabel="api.artifactId">
          <div>
            <p>groupId: <strong>{{api.groupId}}</strong></p>
            <p>artifactId: <strong>{{api.artifactId}}</strong></p>
            <p>Nom de la classe : <strong>{{api.className}}</strong></p>
            <p>Méthode : <strong>{{api.name}}</strong></p>
          </div>
        </q-collapsible>
        <q-collapsible v-if="api.queryParams && api.queryParams.length >  0" label="Query params : "
                       :sublabel="`nb : ${api.queryParams.length}`">
          <div>
            <ul class="query-params">
              <li v-for="query in api.queryParams" :key="query">{{query}}</li>
            </ul>
          </div>
        </q-collapsible>

        <q-collapsible label="Entrée : " :sublabel="`nb : ${parameters.length}`">
          <div>
            <table class="q-table striped">
              <thead>
              <tr>
                <th>Emp.</th>
                <th>Type</th>
                <th>Nom</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="p in parameters">
                <td>{{p.type}}</td>
                <td>{{p.object}}</td>
                <td>{{p.name}}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </q-collapsible>
        <q-collapsible label="Sortie : ">
          <div>
            <p><strong>{{api.returns}}</strong></p>
          </div>
        </q-collapsible>

      </q-list>
    </q-card-main>
  </q-card>
</template>
<script>
  import {
    QCard, QCardTitle, QCardSeparator, QCardMain, QBtn, QCollapsible, QList, QItem, QItemMain
  } from 'quasar';
  import MethodIcon from './MethodIcon'

  export default {
    name: 'ApisCard',
    props: ['api'],
    components: {
      QCard, QCardTitle, QCardSeparator, QCardMain, QBtn, QCollapsible, QList, QItem, QItemMain, MethodIcon
    },
    computed: {
      parameters() {
        if (!this.api.params) {
          return [];
        }
        return JSON.parse(this.api.params);
      }
    }
  }
</script>
<style>

  .apis-card .title {
    display: flex;
    flex-wrap: nowrap;
    justify-content: space-around;
    align-items: center;
    max-width: 35vw;
  }

  .apis-card .title .text {
    word-break: break-all;
    font-weight: 700;
  }

  .apis-card p {
    word-break: break-all;
    max-width: 35vw;
  }

  .apis-card table {
    width: 100%;
  }
</style>
