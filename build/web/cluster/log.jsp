<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>系统日志 &middot; Cassandra可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- Javascript -->
    <script type="text/javascript" src="../assets/js/jquery-1.10.1.js"></script>
    <script type="text/javascript" src="../assets/js/bootstrap.js"></script>
    <script type="text/javascript" src="../assets/js/bootstrap-datetimepicker.min.js"></script>
    <!-- CSS -->
    <link href="../assets/css/bootstrap.css" rel="stylesheet">
    <link href="../assets/css/footer.css" rel="stylesheet">
    <link href="../assets/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css" media="screen">
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
          <li class="active">日志查看</li>
        </ul>
      </div>
      <div class="span8 offset2">
      <label class="radio">
        <input type="radio" name="optionsRadios" id="optionsRadios1" value="option1" checked>
        获取最新的<input class="input-mini" type="text" placeholder="小于500">条日志
      </label>
      <label class="radio">
        <input type="radio" name="optionsRadios" id="optionsRadios2" value="option2">
        获取从
      <div class="input-append" id="datetimepicker2">
        <input data-format="MM/dd/yyyy HH:mm:ss PP" type="text">
        <span class="add-on">
          <i data-date-icon="icon-calendar" data-time-icon="icon-time" class="icon-calendar">
          </i>
        </span>
      </div>
      <script type="text/javascript">
      $(document).ready(function() {
        $(function() {
          $('#datetimepicker2').datetimepicker({
            language: 'en',
            pick12HourFormat: true
          });
        });
      });
      </script>
      开始的<input class="input-mini" type="text" placeholder="小于500">条日志
      </label>
      <p>
      <button class="btn btn-primary pull-right" type="button">获取日志</button>
      </p>
      </br>
      <table class="table table-hover">
        <caption>日志</caption>
          <thead>
            <tr>
              <th>序号</th>
              <th>时间</th>
              <th>日志内容</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>1</td>
              <td>2013/07/02 13:30:40</td>
              <td>dasdasd</td>
            </tr>
            <tr>
              <td>2</td>
              <td>2013/07/02 13:30:41</td>
              <td>dasdasdasdasdadhahdkj</td>
            </tr>

            <tr>
              <td>3</td>
              <td>2013/07/02 13:30:42</td>
              <td>dasdasdasdgjahgjhagjhsgjhas</td>
            </tr>

          </tbody>
      </table>
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
