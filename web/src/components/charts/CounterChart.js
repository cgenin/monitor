// CommitChart.js
import {Line} from 'vue-chartjs';
import Vue from 'vue';


export default Vue.component('counter-chart', {
    extends: Line,
    props: ['service','limit'],
    mounted() {
        this.getStats();
    },
    methods: {
        formatDate(timestamp) {
            let date = new Date(timestamp);
            return date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear() + ' ' + date.getHours() + ':' + date.getMinutes();
        },
        getStats() {
            let filteredMetrics = Object.keys(this.service.metrics)
              .sort((a, b) => a.localeCompare(b))
              .filter(key => key.startsWith('counter') && !key.includes('hystrix'));
            const metrics = filteredMetrics
                .splice(0,this.limit || filteredMetrics.length)
                .reduce((obj, key) => {
                    obj[key] = this.service.metrics[key];
                    return obj;
                }, {});
            this.renderChart({
                labels: Object.keys(metrics).map((key) => key.replace(/(counter\.status\.)([0-9]{3})(\.)/g,'Status $2 : /').replace(/\./g,'/')),
                datasets: [
                    {

                        label: ' requests',
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
                        display: false
                    }]
                }
            })
        }
    },
    watch: {
        service() {
            this.getStats();
        },
        limit(){
            this.getStats();
        }
    }

});
