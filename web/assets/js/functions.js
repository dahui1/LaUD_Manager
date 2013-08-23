
function addEntry (value) {
    var table = document.getElementById("machines");
    var newTr = document.createElement("tr");
    var id = parseInt(table.rows[table.rows.length - 1].children[0].innerText) + 1;
    newTr.id = "Entry" + id;
    var newTd = new Array();
    for (var i = 0; i < 4; i++) {
        newTd[i] = document.createElement("td");
        switch (i) {
        case 0:
        case 1:
        case 2:
            newTd[i].innerHTML = "单击输入值";
            break;
        case 3:
            newTd[i].innerHTML = '<i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode)"></i>';
            break;
        default:
            break;
        }
        newTr.appendChild(newTd[i]);
    }
    table.appendChild(newTr);
    $("td").click(tdclick); 
}

function tdclick(){  
    var td = $(this);  
    var childs = td.context.childNodes;
    var i = childs.length - 1;
    if (i<=0) {
        //1.取出当前的文本内容并且保存起来  
        var text = td.text();  
        //2. 清除当前的td内容  
        td.html("");//也可以用empty()方法  
        //3.建立一个input标签  
        var input = $("<input>");  
        //4.设置文本框里面的值是改写后的内容  
        input.attr("value",text);  
        //4.5响应键盘事件，处理回车  
        input.keyup(function(event){  
            //1.判断是否回车按下  
            //结局不同浏览器获取时间的差异  
            var myEvent = event || window.event;  
            var key = myEvent.keyCode;  
            if(key === 13){  
                var inputNode = $(this);  
                //1.保存当前文本框的内容  
                var inputText = inputNode.val();  
                //2.清空td里面的内容  
                inputNode.parent().html(inputText);  

                td.click(tdclick);  
            }  
        });  

        input.blur(function(){  
            var inputNode = $(this);  
            var inputText = inputNode.val();  
            inputNode.parent().html(inputText);  
            td.click(tdclick);  
        });  

        //5.把文本框就加入到td里面去  
        td.append(input);  
        //6.需要清除td上面的点击事件  
        //6.5高亮数据  

        td.unbind("click");  
        //7.提取文本框里面的值  
    }
     
}  

function removeEntry (r) {
    var con = confirm ("确认删除？");
    if (con === true) {
        var root = r.parentNode.parentNode;
        root.deleteRow(r.rowIndex);
    }
}

function clusterstartup(){
    var table = document.getElementById("machines");
    var length =  table.rows.length;
    var iplist = "";
    var reg=/((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/;
    for( var i = 0 ; i < length ; i ++ ) {
        if( i !== length -1 ){
            var ip=table.rows.item(i).cells.item(0).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确的ip地址");
                return;
            }
            else
                iplist += ip+";";
        }
        else{ 
            var ip=table.rows.item(i).cells.item(0).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确格式的ip地址");
                return;
            }
            else
                iplist += ip;  
        }
    }    
    $.ajax({  
        url:'ClusterStartup.action',  
        type:'POST',  
        data:{"startiplist":iplist},  
        dataType:'json',  
        success:function (data) {  
            alert("is starting...");
        }
    });
}

function clustershutdown(){
    var table = document.getElementById("machines");
    var length =  table.rows.length;
    var iplist = "";
    var reg=/((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/;
    for( var i = 0 ; i < length ; i ++ ) {
        if( i !== length -1 )
        {
            var ip=table.rows.item(i).cells.item(0).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确的ip地址");
                return;
            }
            else
                iplist += ip+";";
        }
        else 
        {
            var ip=table.rows.item(i).cells.item(0).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确格式的ip地址");
                return;
            }
            else
                iplist += ip; 
        }   
    }
    $.ajax({  
        url:'ClusterShutdown.action',  
        type:'POST',  
        data:{"shutiplist":iplist},  
        dataType:'json',  
        success:function (data) {  
            alert("is shutting...");
        }
    });
    
}

function getNodesInfo() {
    $.ajax({  
        url:'NodeStatus',  
        type:'POST',  
        data:"{}",  
        dataType:'json',  
        success:function (data) {  
            $(data).each(function (i, value) {  
                $("#nodesinfo").append("<tr id=\"" + value.endpoint + "\"><td>" + value.endpoint 
                    + "</td><td>" + value.status + "</td><td>" + value.load
                    + "</td><td>" + value.owns + "</td><td>" + value.range 
                    + "</td><td> <button class=\"btn btn-default\" onclick=\"getDetails('" 
                    + value.endpoint + "')\">详细信息</button></td></tr>");  
            });
        },
        error:function() {
            alert("服务器连接失败，请稍后尝试！");
            window.location = "../index.jsp";
        }
    });
}

function getDetails(endPoint) {
    document.getElementById("info").style.display = "none";
    document.getElementById("detail").style.display = "block";
    document.getElementById("all").className = "";
    document.getElementById("one").className = "active";
    document.getElementById("one").innerHTML = "<span class=\"divider\">/</span>" + endPoint + "&nbsp;&nbsp;&nbsp;&nbsp;<a onclick=\"goBack()\">返回完整列表</a>";
    document.getElementById("one").style.display = "inline";
    document.getElementById("nodes").className = "span6 offset4";

    $.ajax({  
        url:'NodeDetail',  
        type:'POST',  
        data:{"endPoint":endPoint},
        dataType:'json',  
        success:function (data) {   
            $("#detailedinfo").append("<tr id=\"" + data.endpoint 
                    + "\"><td>EndPoint</td><td>" + data.endpoint
                    + "</td></tr><td>Token</td><td>" + data.range
                    + "</td></tr><td>UpTime</td><td>" + data.uptime
                    + "</td></tr><td>memMax</td><td>" + data.memMax
                    + "</td></tr><td>memUsed</td><td>" + data.memUsed
                    + "</td></tr><td>datacenter</td><td>" + data.dataCenter
                    + "</td></tr><td>rack</td><td>" + data.rack
                    + "</td></tr><td>status</td><td>" + data.status
                    + "</td></tr><td>state</td><td>" + data.state
                    + "</td></tr><td>load</td><td>" + data.load
                    + "</td></tr><td>owns</td><td>" + data.owns
                    + "</td></tr>"
                );
        },
        error:function() {
            alert("服务器连接失败，请稍后尝试！");
            window.location = "../index.jsp";
        }
    });
}

function goBack() {
    document.getElementById("info").style.display = "table";
    document.getElementById("detail").style.display = "none";
    document.getElementById("detailedinfo").innerHTML = "";
    document.getElementById("all").className = "active";
    document.getElementById("one").className = "";
    document.getElementById("one").innerHTML = "";
    document.getElementById("one").style.display = "none";
    document.getElementById("nodes").className = "span10 offset1";
}

function disconnect(){
     $.ajax({  
        url:'disconnect.action',  
        type:'POST',  
        data:"{}",  
        dataType:'json',  
        success:function (data) {  
            document.getElementById("connected").style.display = "none";
                document.getElementById("unconnected").style.display = "block";
        }
    });
}
