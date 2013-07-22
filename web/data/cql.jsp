<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>CQL查询 &middot; Cassandra可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script type="text/javascript" src="../assets/js/jquery-1.10.1.js"></script>
    <script type="text/javascript" src="../assets/js/bootstrap.js"></script>
    <!-- CSS -->
    <link href="../assets/css/bootstrap.css" rel="stylesheet">
    <link href="../assets/css/footer.css" rel="stylesheet">
    <link href="../assets/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css" media="screen">
   <style type="text/css">
      textarea
      {
        width:95%;
        height:100%;
      }
    </style>
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
          var connected = <%=(String)session.getAttribute("connected")%>;
          if (connected === null) {
            alert("请先连接数据库，再进行查询操作！");
            window.location = "connect.jsp";
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
      <div id="bs">
        <ul class="breadcrumb">
          <li> <span class="divider">/</span></li>
          <li>数据管理<span class="divider">/</span></li>
          <li class="active">执行CQL查询</li>
        </ul>
      </div>
      <div class="span10 offset1" name="cqlinput">
        请输入CQL语句：</br>
        <label><textarea rows="3" ></textarea></br>
        <p>
        <button class="btn btn-primary pull-right" type="button">执行查询</button>
        </p></br>
        <hr>
        <table class="table table-hover">
          <caption>查询结果</caption>
            <thead>
              <tr>
                <th>序号</th>
                <th>结果</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>1</td>
                <td>dasdasd</td>
              </tr>
              <tr>
                <td>2</td>
                <td>dasdasdasdasdadhahdkj</td>
              </tr>

              <tr>
                <td>3</td>
                <td>dasdasdasdgjahgjhagjhsgjhas</td>
              </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div id="push"></div>
    <div id="footer">
      <div class="container">
        <p class="muted credit" align="middle">Developed by LaUD</a>.</p>
      </div>
    </div>
  </div>
</div>
  </body>
</html>
