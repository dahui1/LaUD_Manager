$(function () {
    $(document).ready(function() { 
        $("#lists").append("<li class=\"nav-header\">Keyspaces</li>");
        $.ajax({  
            url:'GetKeyspaces',  
            type:'POST',  
            data:"{}",
            dataType:'json',  
            success:function (data) { 
                $(data).each(function (i, value) {  
                    $("#lists").append("<li><a href=\"#\" onclick=\"getKSDetail('keyspace" + i + "','" + value
                            + "')\" id=\"keyspace" + i + "\">" + value + "</a></li>");  
                    if (i == 0) {
                        document.getElementById("bs").innerHTML = value;
                        getKSDetail("keyspace0", value);
                    }
                });
            },
            error:function() {
                alert("服务器连接失败，请稍后尝试！");
                window.location = "../index.jsp";
            }
        });
    });
});

function getKSDetail(id, keyspace) {
    document.getElementById("bs").innerHTML = document.getElementById(id).innerText;
    $.ajax({  
        url:'GetCFs',  
        type:'POST',  
        data:{"keyspace":keyspace},
        dataType:'json',  
        success:function (data) {  
            document.getElementById("strategy").innerHTML = data.strategy_class;
            document.getElementById("replication").innerHTML = data.replication_factor;
            document.getElementById("CFS").innerHTML = null;
            for (var i = 0; i < data.cfs.length; i++) {
                var value = data.cfs[i];
                var meta = data.cd[i];
                $("#CFS").append(
                    "<div class=\"accordion-group\"><div class=\"accordion-heading\">" + 
                    "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent='#CFS' href='#cf"+i+"'>" +
                    value.name + "</a></div><div id='cf"+i+"'class='accordion-body collapse'>" +
                    "<div class=\"accordion-inner\"><h5>Parameters</h5><table class=\"table table-bordered\"><tbody>" +
                    "<tr><td>Column Type</td><td>" + value.column_type + "</td></tr>" +
                    "<tr><td>Key Validation Class</td><td>" + value.key_validation_class + "</td></tr>" +
                    "<tr><td>Default Column Value Validator</td><td>" + value.default_validation_class + "</td></tr>" +
                    "<tr><td>Row Cache Provider</td><td>" + value.row_cache_provider + "</td></tr>" +
                    "<tr><td>Compaction Strategy</td><td>" + value.compaction_strategy + "</td></tr>" +
                    "<tr><td>Comparator Type</td><td>" + value.comparator_type + "</td></tr>" +
                    "<tr><td>Subcomparator Type</td><td>" + value.subcomparator_type + "</td></tr>" +
                    "<tr><td>Row Cache Size</td><td>" + value.row_cache_size + "</td></tr>" +
                    "<tr><td>Row Cache Save Period in Seconds</td><td>" + value.row_cache_save_period_in_seconds + "</td></tr>" +
                    "<tr><td>Row Cache Keys to Save</td><td>" + value.row_cache_keys_to_save + "</td></tr>" +
                    "<tr><td>Key Cache Size</td><td>" + value.key_cache_size + "</td></tr>" +
                    "<tr><td>Key Cache Save in Seconds</td><td>" + value.key_cache_save_period_in_seconds + "</td></tr>" +
                    "<tr><td>GC Grace Seconds</td><td>" + value.gc_grace_seconds + "</td></tr>" +
                    "<tr><td>Max/Min Compaction Threshold</td><td>" + value.max_compaction_threshold + "/" + value.min_compaction_threshold + "</td></tr>" +
                    "<tr><td>Read Repair Chance</td><td>" + value.read_repair_chance + "</td></tr>" +
                    "<tr><td>Replicate on Write</td><td>" + value.replicate_on_write + "</td></tr>" +
                    "<tr><td>Bloom Filter FP Chance</td><td>" + value.bloom_filter_fp_chance + "</td></tr>" +
                    "</tbody></table><hr><h5>Column MetaData</h5><table class=\"table table-bordered\">" +
                    "<thead><tr><th>Column Name</th><th>Validation Class</th><th>Index Name</th><th>Index Type</th>" +
                    "</tr></thead><tbody id='cfi"+i+"'></tbody></table></div></div></div>");
                for (var j = 0; j < meta.length; j++) {
                    var name = "";
                    var index_name, index_type;
                    for (var k = 0; k < meta[j].name.length; k++)
                        name += String.fromCharCode(meta[j].name[k]);
                    if (meta[j].index_name == null)
                        index_name = "";
                    else
                        index_name = meta[j].index_name;
                    if (meta[j].index_type == null)
                        index_type = "";
                    else
                        index_type = meta[j].index_type;
                    $("#cfi"+i).append(
                        "<tr><td>" + name + "</td><td>" + meta[j].validation_class + 
                        "</td><td>" + index_name + "</td><td>" + index_type + "</td></tr>");
                }
                i++;
            }
        },
        error:function() {
            alert("服务器连接失败，请稍后尝试！");
            window.location = "../index.jsp";
        }
    });
}