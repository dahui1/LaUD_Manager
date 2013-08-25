$(function () {
    $(document).ready(function () {
        $.ajax({
            url: 'GetParams',
            type: 'POST',
            data: "{}",
            dataType: 'json',
            success: function (data) {
                document.getElementById("inputRoot").value = data.rootdir;
                document.getElementById("inputName").value = data.clustername;
                document.getElementById("inputDataDir").value = data.datafiledir;
                document.getElementById("inputCommitDir").value = data.commitlogdir;
                document.getElementById("inputCacheDir").value = data.cachesdir;
                document.getElementById("inputRpcPort").value = data.rpcport;
                document.getElementById("inputSeedCount").value = data.seeds.length;
                if (data.tokens.length == 0 || data.tokens.length != data.seeds.length) {
                    $("#seeds").append("<thead><tr><th>Seeds</th></tr></thead><tbody>");
                    for (var i = 0; i < data.seeds.length; i++) {
                        $("#seeds").append("<tr><td><input type=text class=\"form-control input-xlarge\"" +
                            "id='seeds" + i + "' placeholder='Seeds-" + (i + 1) + "' value='" +
                            data.seeds[i] + "'></td><tr>")
                    }
                } else {
                    document.getElementById("tokenMethod").value = "指定Token";
                    $("#seeds").append("<thead><tr><th>Seeds</th><th>Tokens</th></tr></thead><tbody>");
                    for (var i = 0; i < data.seeds.length; i++) {
                        $("#seeds").append("<tr><td><input type=text class=\"form-control input-medium\"" + "id='seeds" + i + "' placeholder='Seeds-" + (i + 1) + "' value='" + data.seeds[i] + "'></td><td>  <input type=text class=\"form-control input-xlarge\"" + "id='tokens" + i + "' placeholder='Tokens-" + (i + 1) + "' value='" + data.tokens[i] + "'></td></tr>")
                    }
                }
                $("#seeds").append("</tbody>");
            }
        });
    });
});


function showTable() {
    document.getElementById("seeds").innerHTML = null;
    var count = parseInt(document.getElementById("inputSeedCount").value);
    if (isNaN(count))
        return;
    if (document.getElementById("tokenMethod").value == "随机生成Token") {
        $("#seeds").append("<thead><tr><th>Seeds</th></tr></thead><tbody>");
        for (var i = 0; i < count; i++) {
            $("#seeds").append("<tr><td><input type=text class=\"form-control input-xlarge\"" + "id='seeds" + i + "' placeholder='Seeds-" + (i + 1) + "'></td><tr>")
        }
    } else {
        $("#seeds").append("<thead><tr><th>Seeds</th><th>Tokens</th></tr></thead><tbody>");
        for (var i = 0; i < count; i++) {
            $("#seeds").append("<tr><td>  <input type=text class=\"form-control input-medium\"" + "id='seeds" + i + "' placeholder='Seeds-" + (i + 1) + "'>  </td>" + "<td>  <input type=text class=\"form-control input-xlarge\"" + "id='tokens" + i + "' placeholder='Tokens-" + (i + 1) + "'></td></tr>")
        }
    }
    $("#seeds").append("</tbody>");
}

function submitInitParam() {
    var rootdir = document.getElementById("inputRoot").value;
    var clustername = document.getElementById("inputName").value;
    var datafiledir = document.getElementById("inputDataDir").value;
    var commitlogdir = document.getElementById("inputCommitDir").value;
    var cachesdir = document.getElementById("inputCacheDir").value;
    var rpcport = document.getElementById("inputRpcPort").value;
    var count = parseInt(document.getElementById("inputSeedCount").value);
    var seeds = "";
    var tokens = "";
    if (isNaN(count)) {
        alert("请输入种子数！");
        return;
    }
    if (document.getElementById("tokenMethod").value == "随机生成Token") {
        for (var i = 0; i < count; i++) {
            if (document.getElementById("seeds" + i).value == null) {
                alert("请填写所有空格后再提交！")
                return;
            }
            seeds += document.getElementById("seeds" + i).value;
            if (i !== count - 1)
                seeds += ",";
        }
    } else {
        for (var i = 0; i < count; i++) {
            if (document.getElementById("seeds" + i).value == null || document.getElementById("tokens" + i).value == null) {
                alert("请填写所有空格后再提交！")
                return;
            }
            seeds += document.getElementById("seeds" + i).value;
            tokens += document.getElementById("tokens" + i).value;
            if (i !== count - 1) {
                seeds += ",";
                tokens += ",";
            }
        }
    }
    alert("初始化需要一定时间，请稍等……");
    $.ajax({
        url: 'InitCluster',
        type: 'POST',
        data: {
            "rootdir": rootdir,
            "clustername": clustername,
            "datafiledir": datafiledir,
            "commitlogdir": commitlogdir,
            "cachesdir": cachesdir,
            "rpcport": rpcport,
            "seeds": seeds,
            "tokens": tokens
        },
        dataType: 'json',
        success: function (data) {
        }
    });
}