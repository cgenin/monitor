// CommitChart.js
import { Doughnut } from 'vue-chartjs';
import Vue from 'vue';


export default Vue.component('success-chart', {
    extends: Doughnut,
    props: ['service', 'limit'],
    mounted() {
        this.getStats();
    },
    methods: {
        getColor(value) {
            switch (value) {
                case '200':
                    return '#2fee68';
                case '204':
                    return '#36eece';
                case '400':
                    return '#6590ee';
                case '401':
                    return '#ee9f21';
                case '500':
                    return '#ee3e38';
                default:
                    return `#${value}`;
            }
        },
        getStats() {
            const filteredMetrics = Object.keys(this.service.metrics)
                .filter(key => key.startsWith('counter') && !key.includes('hystrix'));
            const metrics = filteredMetrics
                .map(key => ({
                        key: key.replace(/(counter\.status\.)([0-9]{3})(.*)/g, '$2'),
                        value: this.service.metrics[key],
                    })).reduce((statuses, status) => {
                    statuses[status.key] = Number((statuses[status.key] || 0)) + status.value;
                    return statuses;
                }, {});
            this.renderChart({
                    labels: Object.keys(metrics),
                    datasets: [
                        {
                            data: Object.values(metrics),
                            backgroundColor: Object.keys(metrics).map(value => this.getColor(value)),
                            borderWidth: 0,

                        },
                    ],
                }, { responsive: true, maintainAspectRatio: false });
        },
    },
    watch: {
        service() {
            this.getStats();
        },
    },

});
