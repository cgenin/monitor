<template>
  <div class="tables-chart-page">
    <q-card>
      <q-card-main>
        <div class="column items-center">
          <div class="chart-title">
            <h3>Nombre de tables par projets (Median : {{median1}})</h3>
          </div>
          <card-chart
            card-title="Nombre de Table par projet"
            :data="chart1"
            type="pie"
          />
        </div>
      </q-card-main>
    </q-card>
  </div>
</template>
<script>
  import CardChart from '../../components/CardChart'
  import TablesStore from '../../stores/TablesStore'

  export default {
    name: 'TablesChart',
    components: {
      CardChart
    },
    data() {
      return {chart1: {}, median1: 0};
    },
    mounted() {
      TablesStore.groupByProjects()
        .then(chart1 => {
          this.chart1 = chart1;
          const sum = Object.values(chart1).reduce((acc, nb) => acc + nb, 0);
          const res = sum / Object.values(chart1).length;
          this.median1 = Math.floor(res);
        });
    }
  }
</script>
<style scoped>
  .tables-chart-page .chart-title {
    margin-bottom: 1em;
  }
</style>
