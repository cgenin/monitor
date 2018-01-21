<template>
  <canvas ref="chart"></canvas>
</template>
<script>
import Chart from 'chart.js'

export default {
  data () {
    return {
      chart: ''
    }
  },
  props: {
    data: {
      type: Object,
      required: true
    },
    type: {
      default () { return 'bar' }
    },
    cardTitle: {
      default () { return 'Graph' }
    }
  },
  watch: {
    data () {
      this.startChart()
    },
    type () {
      this.chart.destroy()
      this.startChart()
    }
  },
  computed: {
    dataForChart () {
      return {
        labels: Object.keys(this.data),
        datasets: [{
          data: Object.values(this.data),
          backgroundColor: [
            '#2a3f54',
            '#1abb9c',
            '#3498db'
          ],
          borderColor: [
            'rgba(255,99,132,1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)'
          ],
          borderWidth: 1
        }]
      }
    }
  },
  methods: {
    startChart () {
      this.chart = new Chart(this.$refs.chart,
        {
          type: this.type,
          data: this.dataForChart,
          options: {
            legend: {
              display: false
            }
          }
        })
    },
    toImage () {
      window.open(this.chart.toBase64Image());
    }
  }
}
</script>
<style>

</style>
