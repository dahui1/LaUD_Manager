var int;

function getKeyspaces() {
    $.ajax({  
        url:'GetKeyspaces',  
        type:'POST',  
        data:"{}",
        dataType:'json',  
        success:function (data) {  
            $("#lists").append("<li class=\"nav-header\">Keyspaces</li>");
            $(data).each(function (i, value) {  
                $("#lists").append("<li><a href=\"#\" onclick=inputIP('" + value + "') "
                        + "id='keyspace" + i + "'>" + value + "</a></li>");  
            });
        },
        error:function() {
            alert("服务器连接失败，请稍后尝试！");
            window.location = "../index.jsp";
        }
    });
}

function inputIP(keyspace) {
    int = window.clearInterval(int);
    document.getElementById("input").innerHTML = "<h5 class=\"form-signin-heading\">请输入需要监控的机器IP：</h5>"
            + "<input id='ipa' type=\"text\" class=\"input-block-level\" placeholder=\"IP Address\"/>"
            + "<input id='ks' type=\"text\" class=\"input-block-level\" readonly=\"true\"/>"
            + "<button class=\"btn btn-primary\" onclick=\"getIPKS()\">查看</button>";
    document.getElementById("tabtable").style.display = "none";
    document.getElementById("description").style.display = "none";
    document.getElementById("input").style.display = "block";
    document.getElementById("tabs").innerHTML = null;
    document.getElementById("ks").value = keyspace;
    document.getElementById("ipa").value = null;
}

function getIPKS() {
    var ks = document.getElementById("ks").value;
    var ip = document.getElementById("ipa").value;
    int = window.clearInterval(int);
    document.getElementById("input").style.display = "none";
    document.getElementById("tabtable").style.display = "block";
    document.getElementById("tabs").innerHTML = null;
    document.getElementById("graphs").innerHTML = null;
    $("#tabs").append("<li class=\"active\"><a href=\"#statusRL\" data-toggle=\"tab\" onclick=\"drawKSGraph('Read Latency','statusRL','"
            + ks +"','ms','" + ip + "')\">Read Latency</a></li>");
    $("#tabs").append("<li><a href=\"#statusRC\" data-toggle=\"tab\" onclick=\"drawKSGraph('Read Count','statusRC','"
            + ks +"','','" + ip + "')\">Read Count</a></li>");
    $("#tabs").append("<li><a href=\"#statusWL\" data-toggle=\"tab\" onclick=\"drawKSGraph('Write Latency','statusWL','"
            + ks +"','ms','" + ip + "')\">Write Latency</a></li>");
    $("#tabs").append("<li><a href=\"#statusWC\" data-toggle=\"tab\" onclick=\"drawKSGraph('Write Count','statusWC','"
            + ks +"','','" + ip + "')\">Write Count</a></li>");
    $("#graphs").append("<div class=\"tab-pane active\" id=\"statusRL\"></div>");
    $("#graphs").append("<div id=\"statusRC\"></div>");
    $("#graphs").append("<div id=\"statusWL\"></div>");
    $("#graphs").append("<div id=\"statusWC\"></div>");
    drawKSGraph("Read Latency", 'statusRL', ks, "ms", ip);
}

function drawKSGraph(type, id, keyspace, unit, ip) {
    document.getElementById("statusRL").innerHTML = null;
    document.getElementById("statusRC").innerHTML = null;
    document.getElementById("statusWL").innerHTML = null;
    document.getElementById("statusWC").innerHTML = null;
    int = window.clearInterval(int);
    Highcharts.setOptions({  
        global: {  
            useUTC: false  
        }  
    });  
    var chart;  
    chart = new Highcharts.Chart({  
        chart: {  
            renderTo: id,  
            type: 'spline',  
            marginRight: 20,  
            events: {  
                load: function() {  
                    // set up the updating of the chart each second  
                    var series = this.series[0];  
                    int = self.setInterval(function() {  
                        var x = (new Date()).getTime();
                        var y;
                        $.ajax({  
                            url:'KSStatics',  
                            type:'POST',  
                            data:{"type":type, "keyspace":keyspace, "ip":ip},  
                            dataType:'json',  
                            success:function (data) {  
                                y = data;
                                if (y == "NaN")
                                    y = 0;
                                else
                                    y = parseFloat(y);
                                series.addPoint([x, y], true, true);
                            },
                            error:function() {
                                alert("服务器连接失败，请稍后尝试！");
                                window.location = "../index.jsp";
                            }
                        }); 
                    }, 2000);  
                }  
            }  
        },  
        title: {  
            text: '<b>' + type + ' - ' + keyspace + '@' + ip + '</b>'  
        },  
        xAxis: {  
            type: 'datetime',  
            tickPixelInterval: 300  
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
                if (type == "Read Count" || type == "Write Count")
                    return '<b>'+ this.series.name +'</b><br/>'+  
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+  
                    'value: ' + Highcharts.numberFormat(this.y, 0) + unit; 
                else
                    return '<b>'+ this.series.name +'</b><br/>'+  
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+  
                    'value: ' + Highcharts.numberFormat(this.y, 6) + unit;  
            }  
        },  
        legend: {  
            enabled: false  
        },  
        exporting: {  
            enabled: false  
        },  
        series: [{  
            name: type,
            data: (function() {  
                // generate an array of random data  
                var data = [],  
                    time = (new Date()).getTime(),  
                    i;  

                for (i = -9; i <= 0; i++) {  
                    data.push({  
                        x: time + i * 2000, 
                        y : 0
                    });  
                }  
                return data;  
            })()  
        }]  
    });
}

function listEPs() {
    int = window.clearInterval(int);
    document.getElementById("tabtable").style.display = "none";
    document.getElementById("input").style.display = "block";
    document.getElementById("tabs").innerHTML = null;
    document.getElementById("description").style.display = "none";
    document.getElementById("input").innerHTML = "<h5 class=\"form-signin-heading\" id=\"text\">请选择需要监控的机器IP：</h5>"
                    + "请先选择ip：</br><select id=\"ips\" onchange=\"gettpIP()\"></select></br>"
                    + "再选择对应线程池：</br><select id=\"tps\"></select></br>"
                    + "<button class=\"btn btn-primary\" id=\"confirm\" onclick=\"geteptp()\" disabled>查看</button>";
    $.ajax({  
        url:'EndPoints',  
        type:'POST',  
        data:"{}",
        dataType:'json',  
        success:function (data) {  
            $(data).each(function (i, value) {  
                $("#ips").append("<option>" + value + "</option>");  
            });
            gettpThreads(document.getElementById("ips").value)
        },
        error:function() {
            alert("服务器连接失败，请稍后尝试！");
            window.location = "../index.jsp";
        }
    });
}

function gettpIP() {
    int = window.clearInterval(int);
    var ip = document.getElementById("ips").value;
    gettpThreads(ip);
}

function gettpThreads(ip) {
    document.getElementById("tps").innerHTML = null;
    $.ajax({  
        url:'TPThreads',  
        type:'POST',  
        data:{"ip":ip},
        dataType:'json',  
        success:function (data) {  
            $(data).each(function (i, value) {  
                $("#tps").append("<option>" + value + "</option>");  
            });
            document.getElementById("confirm").disabled = false;
        }//,
        //error:function() {
         //   alert("服务器连接失败，请稍后尝试！");
          //  window.location = "../index.jsp";
        //}
    });
}

function geteptp() {
    var ep = document.getElementById("ips").value;
    var tp = document.getElementById("tps").value;
    if (ep == null || tp == null)
        return;
    int = window.clearInterval(int);
    document.getElementById("input").style.display = "none";
    document.getElementById("tabtable").style.display = "block";
    document.getElementById("tabs").innerHTML = null;
    document.getElementById("graphs").innerHTML = null;
    $("#tabs").append("<li class=\"active\"><a href=\"#statusAC\" data-toggle=\"tab\" onclick=\"drawTPGraph('Active Count','statusAC','"
            + ep +"','" + tp + "')\">Active Count</a></li>");
    $("#tabs").append("<li><a href=\"#statusPT\" data-toggle=\"tab\" onclick=\"drawTPGraph('Pending Tasks','statusPT','"
            + ep +"','" + tp + "')\">Pending Tasks</a></li>");
    $("#tabs").append("<li><a href=\"#statusCT\" data-toggle=\"tab\" onclick=\"drawTPGraph('Completed Tasks','statusCT','"
            + ep +"','" + tp + "')\">Completed Tasks</a></li>");
    $("#graphs").append("<div class=\"tab-pane active\" id=\"statusAC\"></div>");
    $("#graphs").append("<div id=\"statusPT\"></div>");
    $("#graphs").append("<div id=\"statusCT\"></div>");
    drawTPGraph("Active Count", 'statusAC', ep, tp);    
}

function drawTPGraph(type, id, ip, tpname) {
    document.getElementById("statusAC").innerHTML = null;
    document.getElementById("statusPT").innerHTML = null;
    document.getElementById("statusCT").innerHTML = null;
    int = window.clearInterval(int);
    Highcharts.setOptions({  
        global: {  
            useUTC: false  
        }  
    });  
    var chart;  
    chart = new Highcharts.Chart({  
        chart: {  
            renderTo: id,  
            type: 'spline',  
            marginRight: 20,  
            events: {  
                load: function() {  
                    // set up the updating of the chart each second  
                    var series = this.series[0];  
                    int = self.setInterval(function() {  
                        var x = (new Date()).getTime();
                        var y;
                        $.ajax({  
                            url:'TPStatics',  
                            type:'POST',  
                            data:{"type":type, "tpname":tpname, "ip":ip},  
                            dataType:'json',  
                            success:function (data) {  
                                y = data.toString();
                                if (y == "NaN")
                                    y = 0;
                                else
                                    y = parseInt(y);
                                series.addPoint([x, y], true, true); 
                            }
//                            error:function() {
//                                alert("服务器连接失败，请稍后尝试！");
//                                window.location = "../index.jsp";
//                            }
                        }); 
                    }, 2000);  
                }  
            }  
        },  
        title: {  
            text: '<b>' + type + ' - ' + tpname + '@' + ip + '</b>'  
        },  
        xAxis: {  
            type: 'datetime',  
            tickPixelInterval: 300  
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
                    'value: ' + Highcharts.numberFormat(this.y, 0);  
            }  
        },  
        legend: {  
            enabled: false  
        },  
        exporting: {  
            enabled: false  
        },  
        series: [{  
            name: type,
            data: (function() {  
                // generate an array of random data  
                var data = [],  
                    time = (new Date()).getTime(),  
                    i;  

                for (i = -9; i <= 0; i++) {  
                    data.push({  
                        x: time + i * 2000, 
                        y : 0
                    });  
                }  
                return data;  
            })()  
        }]  
    });
}

function getClusterSta() {
    int = window.clearInterval(int);
    document.getElementById("input").style.display = "none";
    document.getElementById("tabtable").style.display = "block";
    document.getElementById("description").style.display = "none";
    document.getElementById("tabs").innerHTML = null;
    document.getElementById("graphs").innerHTML = null;
    $("#tabs").append("<li class=\"active\"><a href=\"#statusRL\" data-toggle=\"tab\" onclick=\"drawClusterGraph('Read Latency','statusRL',"
            + "'ms')\">Read Latency</a></li>");
    $("#tabs").append("<li><a href=\"#statusRC\" data-toggle=\"tab\" onclick=\"drawClusterGraph('Read Count','statusRC',"
            + "'')\">Read Count</a></li>");
    $("#tabs").append("<li><a href=\"#statusWL\" data-toggle=\"tab\" onclick=\"drawClusterGraph('Write Latency','statusWL',"
            + "'ms')\">Write Latency</a></li>");
    $("#tabs").append("<li><a href=\"#statusWC\" data-toggle=\"tab\" onclick=\"drawClusterGraph('Write Count','statusWC',"
            + "'')\">Write Count</a></li>");
    $("#graphs").append("<div class=\"tab-pane active\" id=\"statusRL\"></div>");
    $("#graphs").append("<div id=\"statusRC\"></div>");
    $("#graphs").append("<div id=\"statusWL\"></div>");
    $("#graphs").append("<div id=\"statusWC\"></div>");
    drawClusterGraph("Read Latency", 'statusRL', "ms");
}

function drawClusterGraph(type, id, unit) {
    document.getElementById("statusRL").innerHTML = null;
    document.getElementById("statusRC").innerHTML = null;
    document.getElementById("statusWL").innerHTML = null;
    document.getElementById("statusWC").innerHTML = null;
    int = window.clearInterval(int);
    Highcharts.setOptions({  
        global: {  
            useUTC: false  
        }  
    });  
    var chart;  
    chart = new Highcharts.Chart({  
        chart: {  
            renderTo: id,  
            type: 'spline',  
            marginRight: 20,  
            events: {  
                load: function() {  
                    // set up the updating of the chart each second  
                    var series = this.series[0];  
                    int = self.setInterval(function() {  
                        var x = (new Date()).getTime();
                        var y;
                        $.ajax({  
                            url:'ClusterStatics',  
                            type:'POST',  
                            data:{"type":type},  
                            dataType:'json',  
                            success:function (data) {  
                                y = data;
                                if (y == "NaN")
                                    y = 0;
                                else
                                    y = parseFloat(y);
                                series.addPoint([x, y], true, true);
                            },
                        }); 
                    }, 2000);  
                }  
            }  
        },  
        title: {  
            text: '<b>' + type + '</b>'  
        },  
        xAxis: {  
            type: 'datetime',  
            tickPixelInterval: 300  
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
                if (type == "Read Count" || type == "Write Count")
                    return '<b>'+ this.series.name +'</b><br/>'+  
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+  
                    'value: ' + Highcharts.numberFormat(this.y, 0) + unit; 
                else
                    return '<b>'+ this.series.name +'</b><br/>'+  
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) +'<br/>'+  
                    'value: ' + Highcharts.numberFormat(this.y, 6) + unit;  
            }  
        },  
        legend: {  
            enabled: false  
        },  
        exporting: {  
            enabled: false  
        },  
        series: [{  
            name: type,
            data: (function() {  
                // generate an array of random data  
                var data = [],  
                    time = (new Date()).getTime(),  
                    i;  

                for (i = -9; i <= 0; i++) {  
                    data.push({  
                        x: time + i * 2000, 
                        y : 0
                    });  
                }  
                return data;  
            })()  
        }]  
    });
}
