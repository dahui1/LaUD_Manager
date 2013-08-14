<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>实时监控数据 &middot; LaUD可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery-1.10.1.js"></script>
    <script src="../assets/js/bootstrap.js"></script>
    <script src="../assets/js/highcharts/highcharts.js"></script>
    <script src="../assets/js/highcharts/exporting.js"></script>
    <script src="../assets/js/monitor.js"></script>

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
          <li class="active">实时监控数据</li>
        </ul>
      </div>
      <div class="row">
        <div class="span2">
            <ul class="nav nav-list" id="lists">
                <li class="nav-header">Cluster</li>
                <li><a href="#">系统状态监控</a></li>
                <li class="nav-header">Thread Pools</li>
                <li><a href="#" onclick="listEPs()">线程池监控</a></li>
            </ul>
      </div>
      <script>
        $(function () {
           $(document).ready(function() {
               getKeyspaces();
           });
        });
      </script>
      <div class="span10">
        
        <div id="input" style="display: none" class="span5 offset1">
        <h5 class="form-signin-heading" id="text">请输入需要监控的机器IP：</h5>
        <input id='ipa' name="ip" type="text" class="input-block-level" placeholder="IP Address"/>
        <input id='ks' type="text" class="input-block-level" readonly="true"/>
        <button class="btn btn-primary" onclick="getIPKS()">查看</button>
        </div>
        <div class="tabbable" id="tabtable" style="display: none">
          <ul class="nav nav-tabs" id="tabs">
          </ul>
          <div class="tab-content" id="graphs">
          </div>
        </div>
      </div>
      </div>
      <div id="push"></div>
    </div>
  </div>
    <div id="footer">
      <div class="container">
        <p class="muted credit" align="middle">Developed by LaUD</a>.</p>
      </div>
    </div>
  </body>
</html>
