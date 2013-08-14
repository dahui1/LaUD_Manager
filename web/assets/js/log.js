function showloglist(){
    var ip=$("#logip").val();
    var reg=/((25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]\d|\d)/;
    if(!reg.test(ip)){
                alert("请输入正确的ip地址");
                return;
      }
       
     $.ajax({  
        url:'GetLogList.action',  
        type:'POST',  
        data:{"ip":ip},  
        dataType:'json',  
        success:function (data) {
            document.getElementById("logs").innerHTML="";
           $(data.loglist).each(function(i,value){
              $("#logs").append(value);
            });
              
        }
    });
}

function openlog(filename){
   var logfilename=filename;
   $.ajax({  
        url:'OpenLog.action',  
        type:'POST',  
        data:{"logfilename":logfilename},  
        dataType:'json',  
        success:function (data) {
           document.getElementById("logmodalbody").innerHTML="";
           document.getElementById("logfilename").innerHTML=filename;
           $(data.logcontent).each(function(i,value){
              $("#logmodalbody").append(value);
            });
            $("#logmodal").modal('toggle');  
        }
    });
   
}