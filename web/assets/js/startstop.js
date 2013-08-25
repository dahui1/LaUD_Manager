$(function () {
    $(document).ready(function () {
        $.ajax({
            url: 'AllEndPoints',
            type: 'POST',
            data: "{}",
            dataType: 'json',
            success: function (data) {
                $(data).each(function (i, value) {
                    $("#machines").append(
                            "<tr id=\"entry" + i + "\"><td>" + value +
                            "</td><td> usdms </td><td> hgjusdms </td>" +
                            "<td> <i class=\"icon-remove\" onclick=\"removeEntry(this.parentNode.parentNode)\"></i> </td></tr>");
                });
                $("td").click(tdclick); 
            },
            error: function () {
                alert("无法获取集群机器的IP列表，请自行输入需要开启或关闭的机器！");
            }
        });

    });
});


function clusterstartup() {
    var table = document.getElementById("machines");
    var length = table.rows.length;
    var iplist = "";
    var reg = /((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/;
    for (var i = 0; i < length; i++) {
        if (i !== length - 1) {
            var ip = table.rows.item(i).cells.item(0).innerHTML;
            ip = ip.replace(/ +/, "");
            if (!reg.test(ip)) {
                alert("请输入正确的ip地址");
                return;
            } else
                iplist += ip + ";";
        } else {
            var ip = table.rows.item(i).cells.item(0).innerHTML;
            ip = ip.replace(/ +/, "");
            if (!reg.test(ip)) {
                alert("请输入正确格式的ip地址");
                return;
            } else
                iplist += ip;
        }
    }
    $.ajax({
        url: 'ClusterStartup.action',
        type: 'POST',
        data: {
            "startiplist": iplist
        },
        dataType: 'json',
        success: function (data) {
            alert("正在开启列表中的机器，请稍后在\"集群管理->集群状态\"中查看对应机器状态！");
        }
    });
}

function clustershutdown() {
    var table = document.getElementById("machines");
    var length = table.rows.length;
    var iplist = "";
    var reg = /((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/;
    for (var i = 0; i < length; i++) {
        if (i !== length - 1) {
            var ip = table.rows.item(i).cells.item(0).innerHTML;
            ip = ip.replace(/ +/, "");
            if (!reg.test(ip)) {
                alert("请输入正确的ip地址");
                return;
            } else
                iplist += ip + ";";
        } else {
            var ip = table.rows.item(i).cells.item(0).innerHTML;
            ip = ip.replace(/ +/, "");
            if (!reg.test(ip)) {
                alert("请输入正确格式的ip地址");
                return;
            } else
                iplist += ip;
        }
    }
    $.ajax({
        url: 'ClusterShutdown.action',
        type: 'POST',
        data: {
            "shutiplist": iplist
        },
        dataType: 'json',
        success: function (data) {
            alert("正在开启列表中的机器，请稍后在\"集群管理->集群状态\"中查看对应机器状态！");
        }
    });

}