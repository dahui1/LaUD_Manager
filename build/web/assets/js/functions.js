
function addEntry (value) {
    if (value === 2) {
        var table = document.getElementById("addresses");
        var rows = table.rows.length + 1;
        var newTr = document.createElement("tr");
        var newTd= document.createElement("td");
        newTd.innerHTML = '<input type="text" class="input-medium" placeholder="IP" name="ip' + rows
                + '">  <input type="text" class="input-mini" placeholder="Port" name="port' + rows + '">';
        newTr.appendChild(newTd);
        table.appendChild(newTr);
    }
    
    if (value === 6) {
        var table = document.getElementById("machines");
        var newTr = document.createElement("tr");
        var id = parseInt(table.rows[table.rows.length - 1].children[0].innerText) + 1;
        newTr.id = "Entry" + id;
        var newTd = new Array();
        for (var i = 0; i < 6; i++) {
            newTd[i] = document.createElement("td");
            switch (i) {
            case 0:
                newTd[i].innerHTML = id;
                break;
            case 1:
            case 2:
            case 3:
                newTd[i].innerHTML = "单击输入值";
                break;
            case 4:
                newTd[i].innerHTML = '<span class="label"> 未知 </span>';
                break;
            case 5:
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
            var ip=table.rows.item(i).cells.item(1).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确的ip地址");
                return;
            }
            else
                iplist += ip+";";
        }
        else{ 
            var ip=table.rows.item(i).cells.item(1).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确格式的ip地址");
                return;
            }
            else
                iplist += ip;  
        }
    }    
    var http2;
    if(window.ActiveXObject){
        http2=new ActiveXObject("Microsoft.XMLHTTP");
    } else if(window.XMLHTTPRequest){
        http2=new XMLHttpRequest();
    }

    var params2 = "startiplist="+iplist;

    http2.open("POST", "ClusterStartup.action", true);
    http2.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http2.setRequestHeader("Content-length", params2.length);
    http2.setRequestHeader("Connection", "close");
    http2.onreadystatechange = function(){
        if (http2.readyState === 4 && http2.status === 200){
           alert("is starting...");
        }
    }
    http2.send(params2);
}

function clustershutdown(){
    var table = document.getElementById("machines");
    var length =  table.rows.length;
    var iplist = "";
    var reg=/((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/;
    for( var i = 0 ; i < length ; i ++ ) {
        if( i !== length -1 )
        {
            var ip=table.rows.item(i).cells.item(1).innerHTML;
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
            var ip=table.rows.item(i).cells.item(1).innerHTML;
            ip=ip.replace(/ +/,"");
            if(!reg.test(ip)){
                alert("请输入正确格式的ip地址");
                return;
            }
            else
                iplist += ip; 
        }   
    }    
    var http2;
    if(window.ActiveXObject){
        http2=new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if(window.XMLHTTPRequest){
        http2=new XMLHttpRequest();
    }
    var params2 = "shutiplist="+iplist;

    http2.open("POST", "ClusterShutdown.action", true);

    http2.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http2.setRequestHeader("Content-length", params2.length);
    http2.setRequestHeader("Connection", "close");
    http2.onreadystatechange = function(){
        if (http2.readyState === 4 && http2.status === 200){
           alert("is shutting...");
        }
    }
    http2.send(params2);
}

function getNodesInfo() {
    $.ajax({  
        url:'NodeStatus',  
        type:'POST',  
        data:"{}",  
        dataType:'json',  
        success:function (data) {  
            $(data.infos).each(function (i, value) {  
                $("#nodesinfo").append("<tr id=\"" + value.endpoint + "\"><td>" + value.endpoint + "</td><td>" + value.status 
                    + "</td><td>" + value.state + "</td><td>" + value.load
                    + "</td><td>" + value.owns + "</td><td>" + value.range 
                    + "</td><td> <button class=\"btn btn-default\" onclick=\"getDetails('" 
                    + value.endpoint + "')\">详细信息</button></td></tr>");  
            });
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
            $("#detailedinfo").append("<tr id=\"" + data.info.endpoint 
                    + "\"><td>EndPoint</td><td>" + data.info.endpoint
                    + "</td></tr><td>Token</td><td>" + data.info.range
                    + "</td></tr><td>UpTime</td><td>" + data.info.uptime
                    + "</td></tr><td>memMax</td><td>" + data.info.memMax
                    + "</td></tr><td>memUsed</td><td>" + data.info.memUsed
                    + "</td></tr><td>datacenter</td><td>" + data.info.dataCenter
                    + "</td></tr><td>rack</td><td>" + data.info.rack
                    + "</td></tr><td>status</td><td>" + data.info.status
                    + "</td></tr><td>state</td><td>" + data.info.state
                    + "</td></tr><td>load</td><td>" + data.info.load
                    + "</td></tr><td>owns</td><td>" + data.info.owns
                    + "</td></tr>"
                );
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