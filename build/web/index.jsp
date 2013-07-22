<%@page language="java" import="java.util.*" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<html lang="en">
  <head>
    <meta charset="utf-8">
    <base href="<%=basePath%>">
    <title>Cassandra可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <script src="assets/js/jquery.js"></script>
    <script src="assets/js/bootstrap.js"></script>
    <!-- CSS -->
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <link href="assets/css/footer.css" rel="stylesheet">
    <link href="assets/css/bootstrap-responsive.css" rel="stylesheet">

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
              window.location = "signin.jsp";
          }
      </script>
    <div id="wrap">

      <!-- Fixed navbar -->
      <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
          <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            </button>
            <a class="brand" href="./index.jsp">LaUD Manager</a>
            <div class="nav-collapse collapse">
            <p class="navbar-text pull-right">
                欢迎光临, <%
                    out.println(session.getAttribute("user"));
                    %>
            </p>
              <ul class="nav">
                <li class="active"><a href="./index.jsp">首页</a></li>
                <li class="divider-vertical"></li>
                <li class="dropdown">
                  <a class="dropdown-toggle" data-toggle="dropdown"> 集群管理 <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                    <li><a href="cluster/startstop.jsp"> 启动/关闭集群 </a></li>
                    <li><a href="cluster/status.jsp"> 集群状态 </a></li>
                    <li><a href="cluster/monitor.jsp"> 集群监控 </a></li>
                    <li><a href="cluster/log.jsp"> 日志查看 </a></li>
                  </ul>
                </li>
                <li class="divider-vertical"></li>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown"> 数据管理 <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                    <li><a href="data/cql.jsp">CQL查询</a></li>
                    <li><a href="data/connect.jsp">连接数据库</a></li>
                    <li class="dropdown-submenu">
                        <a tabindex="-1" href="#">数据库信息</a>
                        <ul class="dropdown-menu pull-right">
                            <li><a tabindex="-1" href="data/dbinfo.jsp">LaUD KV</a></li>
                            <li><a tabindex="-1" href="#">LaUD Object</a></li>
                            <li><a tabindex="-1" href="#">LaUD ..</a></li>

                        </ul>
                    </li>
                  </ul>
                </li>
                <li class="divider-vertical"></li>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown"> 参数管理 <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                    <li><a href="param/paraconfig.jsp"> 参数配置 </a></li>
                    <li><a href="param/paratuning.jsp"> 参数调优 </a></li>
                  </ul>
                </li>
                <li class="divider-vertical"></li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Begin page content -->
      <div class="container">
        <div class="row">
          <div class="span10 offset1" >
            <div class="hero-unit">
              <h2>欢迎使用Cassandra集群可视化管理工具!</h2>
              <p>此工具…….</p>
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="cluster/startstop.jsp">开启/关闭集群 </a></h4>
                <p>一键开启或关闭集群. </p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="cluster/status.jsp">实时查看集群状态 </a></h4>
                <p>查看集群状态，并实时进行更新. </p>
              </div><!--/span-->
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="cluster/log.jsp"> 查看集群运行日志 </a></h4>
                <p>查看一段时间内系统运行日志. </p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="data/cql.jsp"> 执行CQL语句查询 </a></h4>
                <p>在数据库中执行CQL语句查询，并返回结果. </p>
              </div><!--/span-->
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="param/paraconfig.jsp">调整集群参数 </a></h4>
                <p>通过参数列表对集群的参数进行手动调整. </p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="param/paratuning.jsp">进行系统参数调优 </a></h4>
                <p> 通过我们的方法对参数进行辅助调优. </p>
              </div><!--/span-->
            </div><!--/row-->
          </div><!--/span-->
        </div><!--/row-->
      </div>
      
       <div id="push"></div>
    </div>

    <div id="footer">
      <div class="container">
        <p class="muted credit" align="middle">Developed by LaUD</a>.</p>
      </div>
    </div>

  </body>
</html>
