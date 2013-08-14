<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>开启/关闭集群 &middot; LaUD可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery-1.10.1.js"></script>
    <script src="../assets/js/functions.js"></script>
    <script src="../assets/js/bootstrap.js"></script>

    <!-- CSS -->
    <link href="../assets/css/bootstrap.css" rel="stylesheet">
    <link href="../assets/css/footer.css" rel="stylesheet">
    <link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

  </head>
  <body>
      <script>
        var user = <%=(String)session.getAttribute("user")%>;
        if (user === null) {
            alert("请先登录！");
            window.location = "../signin.jsp";
        }
        $(function(){  
            //找到所有的td节点  
            $("td").click(tdclick);  
        }); 

      </script>
    <div id="wrap">

      <!-- Fixed navbar -->
      <%@include file="../template.jsp" %>
      <script>
          document.getElementById("cluster").className = "dropdown active"; 
      </script>
    <!-- Begin page content -->
    <div class="container">
      <div id="bs">
        <ul class="breadcrumb">
          <li> <span class="divider">/</span></li>
          <li>集群管理<span class="divider">/</span></li>
          <li class="active">开启|关闭集群</li>
        </ul>
      </div>
      <div class="span8 offset2">
        <!--p>当前集群中运行的机器状态为：</p-->
        <table class="table table-bordered" id="iptable">
          <thead>
            <tr>
              
              <th> 节点IP </th>
              <th> 用户名 </th>
              <th> 密码 </th>
              <th>  </th>
            </tr>
          </thead>
          <tbody id="machines">
            <tr id="entry1">
             
              <td> 192.168.3.1 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode)"></i> </td>
            </tr>
            <tr id="entry2">
             
              <td> 192.168.3.13 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode)"></i> </td>
            </tr>
            <tr id="entry3">
            
              <td> 192.168.3.15 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode)"></i> </td>
            </tr>
            <tr id="entry4">
              
              <td> 192.168.3.29 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode)"></i> </td>
            </tr>
            <tr id="entry5">
              
              <td> 192.168.3.36 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode);"></i> </td>
            </tr>
            <tr id="entry6">
            
              <td> 192.168.3.38 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode);"></i> </td>
            </tr>
            <tr id="entry7">
              
              <td> 192.168.3.42 </td>
              <td> usdms </td>
              <td> hgjusdms </td>
              <td> <i class="icon-remove" onclick="removeEntry(this.parentNode.parentNode);"></i> </td>
            </tr>
          </tbody>
        </table>

        <!--i class="icon-refresh"></i--> <i class="icon-plus" onclick="addEntry(6);"></i>
        <p>你可以进行以下操作：</p>
        <ul class="nav nav-tabs nav-stacked">
           <li><a href="#"  onclick="javascript:clusterstartup();" >启动列表中的机器</a></li>
          <li><a href="#" onclick="javascript:clustershutdown();">关闭列表中的机器</a></li>
          <li><a href="status.jsp">查看集群中各节点状态</a></li>
        </ul>
      </div>
       
    </div>
  </div>
      <div id="push"></div>
    <div id="footer">
      <div class="container">
        <p class="muted credit" align="middle">Developed by LaUD</p>
      </div>
    </div>
  </body>
</html>
