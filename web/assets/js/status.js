$(function () {
    $(document).ready(function() {  
        Highcharts.setOptions({  
            global: {  
                useUTC: false  
            }  
        });  
        var chart;  
        chart = new Highcharts.Chart({  
            chart: {  
                renderTo: 'statusA',  
                type: 'spline',  
                marginRight: 10,  
                events: {  
                    load: function() {  
                        // set up the updating of the chart each second  
                        var series = this.series[0];  
                        setInterval(function() {  
                            var x = (new Date()).getTime(), // current time  
                                y = Math.random();  
                            series.addPoint([x, y], true, true);  
                        }, 1000);  
                    }  
                }  
            },  
            title: {  
                text: '<b>Real Time Status 1 of the Cluster</b>'  
            },  
            xAxis: {  
                type: 'datetime',  
                tickPixelInterval: 150  
            },  
            yAxis: {
                plotLines: [{  
                    value: 0,  
                    width: 1,  
                    color: '#808080'  
                }]  
            },  
            tooltip: {  
                formatter: function() {  
                        return '<b>'+ this.series.name +'</b><br/>'+  
                        Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+  
                        Highcharts.numberFormat(this.y, 2);  
                }  
            },  
            legend: {  
                enabled: false  
            },  
            exporting: {  
                enabled: false  
            },  
            series: [{  
                name: 'status',  
                data: (function() {  
                    // generate an array of random data  
                    var data = [],  
                        time = (new Date()).getTime(),  
                        i;  
      
                    for (i = -19; i <= 0; i++) {  
                        data.push({  
                            x: time + i * 1000,  
                            y: Math.random()  
                        });  
                    }  
                    return data;  
                })()  
            }]  
        });  
    });  
    
});