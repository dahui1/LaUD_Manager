<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>数据库信息 &middot; LaUD可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery-1.10.1.js"></script>
    <script src="../assets/js/bootstrap.js"></script>
    <script src="../assets/js/dbinfo.js"></script>

    <!-- CSS -->
    <link href="../assets/css/bootstrap.css" rel="stylesheet">
    <link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="../assets/css/footer.css" rel="stylesheet">
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
          document.getElementById("data").className = "dropdown active"; 
      </script>
    <!-- Begin page content -->
    <div class="container">
      <div>
        <ul class="breadcrumb">
          <li> <span class="divider">/</span></li>
          <li>数据管理<span class="divider">/</span></li>
          <li>LaUD KV 信息<span class="divider">/</span></li>
          <li class="active" id="bs"></li>
        </ul>
      </div>
      <div class="row">
        <div class="span2">
          <ul class="nav nav-list" id="lists">
          </ul>
        </div>
        <div class="span10" id="ks1">
          <h5>Keyspace Settings</h5>
          <table class="table table-bordered">
            <tbody>
              <tr>
                <td>replica_placement_strategy</td>
                <td id="strategy"></td>
              </tr>
              <tr>
                <td>replication_factor</td>
                <td id="replication"></td>
              </tr>
            </tbody>
          </table>
          <hr>
          <h5>Column Families</h5>
        <div class="accordion" id="CFS">

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