<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>实时监控数据 &middot; Cassandra可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery.js"></script>
    <script src="../assets/js/bootstrap.js"></script>
    <script src="../assets/js/status.js"></script>
    <script src="../assets/js/highcharts/highcharts.js"></script>
    <script src="../assets/js/highcharts/exporting.js"></script>

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
      <div class="tabbable tabs-left">
        <ul class="nav nav-tabs">
          <li class="active"><a href="#statusA" data-toggle="tab">Status 1</a></li>
          <li><a href="#statusB" data-toggle="tab">Status 2</a></li>
          <li><a href="#statusC" data-toggle="tab">Status 3</a></li>
        </ul>
        <div class="tab-content">
          <div class="tab-pane active" id="statusA">
          </div>
          <div class="tab-pane" id="statusB">
            <p>There is nothing yet</p>
          </div>
          <div class="tab-pane" id="statusC">
            <p>There is nothing yet</p>
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
