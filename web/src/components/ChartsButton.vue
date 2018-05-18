<template>
  <div class="row align-stretch">
    <q-btn class="btn-version" @click="openModal" color="secondary" v-if="serviceName && server" small>
        <span
          v-if="serviceInfos.info && serviceInfos.info.build">
          <strong>{{serviceInfos.info.build.version}}</strong>
          <br>
          <span>{{formatYYYYMMDDHHmm(serviceInfos.info.build.timestamp)}}</span>
        </span>
    </q-btn>
    <markdown-button @click="openProperties" icon="list" color="orange-4" title="Properties" :content="properties"/>
    <q-modal @hide="closingModal"
             ref="layoutModal"
             v-model="modal"
             :content-css="{minWidth: '55vw', minHeight: '85vh', padding:'1em'}">
      <q-modal-layout>
        <q-toolbar slot="header">
          <div class="q-toolbar-title">
            "{{serviceName}}" sur le serveur {{server}}
          </div>
          <q-btn flat @click="modal= false">
            <q-icon name="close"/>
          </q-btn>
        </q-toolbar>
        <div class="layout-padding">
          <q-inner-loading :visible="loading">
            <q-spinner-gears size="50px" color="primary"/>
          </q-inner-loading>
          <div v-if="service && modal && displayGraph">
            <h2>Temps de réponse</h2>
            <div>Moyenne : {{getAverage()}}ms</div>
            <div>80% : {{get80()}}ms</div>
            <hr>
            <response-time-chart
              :service="service">
            </response-time-chart>
            <h2>Nombre de requête par url</h2>
            <counter-chart
              :service="service">
            </counter-chart>
            <h2>Répartition des status HTTP</h2>
            <success-chart
              :service="service">
            </success-chart>
          </div>
        </div>
      </q-modal-layout>
    </q-modal>
  </div>
</template>
<script>
  import ResponseTimeChart from './charts/ResponseTimeChart';
  import CounterChart from './charts/CounterChart';
  import SuccessChart from './charts/SuccessChart';
  import ServicesStore from '../stores/ServicesStore';
  import {formatYYYYMMDDHHmm, mavenToDate} from '../Dates';
  import MarkdownButton from "./MarkdownButton";

  export default {
    name: 'ChartsButton',
    data() {
      return {
        modal: false,
        service: null,
        displayGraph: false,
        loading: false,
        properties: ' '
      };
    },
    props: ['serviceName', 'server', 'serviceInfos'],
    components: {
      MarkdownButton,
      ResponseTimeChart, CounterChart, SuccessChart
    },
    methods: {
      async loadServiceData() {
        this.loading = true;
        if (this.serviceName && this.server) {
          await ServicesStore.getService(this.serviceName)
            .then((service) => {
              this.service = service.servers.find((s) => s.host === this.server);
            }).catch(() => {
              console.error("Error while recuperating datas");
            })
            .then(() => {
              // Sans le timeout je n'arrive pas a afficher le graph...
              setTimeout(() => {
                this.loading = false;
                this.displayGraph = true;
              }, 0);
            })
        }
      },
      getAverage() {
        const metrics = this.getMetrics();
        return metrics.reduce((a, b) => a + b, 0) / metrics.length;
      },
      get80() {
        const metrics = this.getMetrics();
        return metrics
          .sort((a, b) => a - b)
          [Math.floor(metrics.length * 0.80)];
      },
      getMetrics() {
        let filteredMetrics = Object.keys(this.service.metrics)
          .filter(key => key.startsWith('gauge') && !key.includes('hystrix'));
        return filteredMetrics
          .reduce((obj, key) => {
            obj.push(this.service.metrics[key]);
            return obj;
          }, []);
      },
      openModal() {
        this.modal = true;
        this.loadServiceData();
      },
      formatKeys(a) {
        return (Object.keys(a)
          .sort((a, b) => a.localeCompare(b))
          .reduce((a1, b1) => `
${a1}
${b1}=${a[b1]}
`, ''));
      },
      async openProperties() {
        this.properties = 'Chargement des données en cours...';
        await this.loadServiceData();
        let env = Object.keys(this.service.env)
          .filter((k) => k !== 'systemProperties' && k !== 'systemEnvironment' && k !== 'servletContextInitParams')
          .sort((a, b) => a.localeCompare(b))
          .reduce((a, b) => `
${a}
----
${b}
----
${this.formatKeys(this.service.env[b])}
          `, '');
        this.properties = env;
      },
      closingModal() {
        this.displayGraph = false;
      },
      formatYYYYMMDDHHmm(date) {
        return formatYYYYMMDDHHmm(mavenToDate(date));
      }

    },
    mounted() {
    }
  }
</script>

<style lang="stylus">
  .btn-version
    margin-right 15px
    min-width 146px
</style>
