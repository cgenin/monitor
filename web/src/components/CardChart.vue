<template>
  <canvas ref="chart"></canvas>
</template>
<script lang="ts">
  import Vue from 'vue'
  import Component from 'vue-class-component';
  import Chart from 'chart.js';
  import {Prop, Watch} from "vue-property-decorator";

  @Component
  export default class CardChart extends Vue {
    chart: any = '';
    @Prop({type: Object, required: true,}) data?: any;
    @Prop({default: 'bar'}) type: string;
    @Prop({default: 'Graph'}) cardTitle: string;

    @Watch('data')
    onChangeData() {
      this.startChart();
    }

    @Watch('type')
    onChangeType() {
      this.chart.destroy();
      this.startChart();
    }

    get dataForChart() {
      return {
        labels: Object.keys(this.data),
        datasets: [{
          data: Object.values(this.data),
          backgroundColor: [
            '#2a3f54',
            '#1abb9c',
            '#3498db',
            '#FFEBEE',
            '#bf360c',
            '#aaaaaa',
            '#8c9eff',
            '#1b5e20',
            '#C2185B',
            '#FF5252',
            '#F8BBD0',
            '#689F38',
            '#536DFE',
            '#8BC34A',

          ],
          borderColor: [
            'rgba(255,99,132,1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
          ],
          borderWidth: 1,
        }],
      };
    }

    startChart() {
      this.chart = new Chart(
        this.$refs.chart,
        {
          type: this.type,
          data: this.dataForChart,
          options: {
            legend: {
              display: false,
            },
          },
        },
      );
    }

    toImage() {
      window.open(this.chart.toBase64Image());
    }
  }
</script>
<style>

</style>
