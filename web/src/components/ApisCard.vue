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
      <div class="text-comment">
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
            <table class="params-table">
              <thead>
              <tr>
                <th>Emp.</th>
                <th>Type</th>
                <th>Nom</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="p in parameters" :key="p.name">
                <td class="text-secondary">{{p.type}}</td>
                <td class="text-secondary">{{p.object}}</td>
                <td class="text-secondary">{{p.name}}</td>
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
<script lang="ts">
  import Vue from 'vue';
  import Component from 'vue-class-component';
  import MethodIcon from './MethodIcon';
  import { ApiDto } from '../store/microservices/types';
  import {Prop} from "vue-property-decorator";

  @Component({
    components: {
      MethodIcon,
    },
  })
  export default class ApisCard extends Vue {
   @Prop() api: ApiDto;

    get parameters() {
      if (!this.api.params) {
        return [];
      }
      return JSON.parse(this.api.params);
    }
  }
</script>
<style>
  .apis-card {
    margin-top: 2px;
  }

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

  .apis-card .text-comment {
    margin-top: 10px;
  }

  .apis-card p {
    word-break: break-all;
    max-width: 35vw;
  }

  .apis-card table {
    width: 100%;
  }

  .apis-card .params-table th {
    background-color: #0c0c0c;
    color: white;
    padding: 5px;
    border-radius: 4px;
  }

  .apis-card .params-table td {
    text-align: center;
    font-weight: bold;
  }

  .apis-card .params-table th,
  .apis-card .params-table td {
    padding: 5px;
  }
</style>
