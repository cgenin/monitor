// CommitChart.js
import {Line} from 'vue-chartjs';
import Vue from 'vue';


export default Vue.component('response-time-chart', {
    extends: Line,
    props: ['service'],
    mounted() {
        this.getStats();
    },
    methods: {
        getStats() {
            let service = this.service;
            let filteredMetrics = Object.keys(service.metrics)
                .filter(key => key.startsWith('gauge') && !key.includes('hystrix'))
                .sort((a, b) => service.metrics[a] - service.metrics[b])
            ;
            const metrics = filteredMetrics
                .splice(0, this.limit || filteredMetrics.length)
                .reduce((obj, key) => {
                    obj[key] = service.metrics[key];
                    return obj;
                }, {});
            this.renderChart({
                labels: Object.keys(metrics).map((key) => key.replace(/(gauge\.response\.)/g, '').replace(/\./g, '/')),
                datasets: [
                    {

                        label: 'ms',
                        backgroundColor: 'white',
                        borderColor: '#ff5722',
                        data: Object.values(metrics)
                    }
                ]
            }, {
                animation: {
                    duration: 0, // general animation time
                },
                hover: {
                    animationDuration: 0, // duration of animations when hovering an item
                },
                responsiveAnimationDuration: 0, // animation duration after a resize
                responsive: true,
                maintainAspectRatio: false,
                legend: {
                    display: false
                },
                scales: {
                    xAxes: [{
                        display: false,
                    }],
                },
                layout: {
                    padding: {
                        left: 0,
                        right: 10,
                        top: 0,
                        bottom: 0
                    }
                }

            })
        }
    },
    watch: {
        service() {
            this.getStats();
        },
        limit() {
            this.getStats();
        }
    }

});
