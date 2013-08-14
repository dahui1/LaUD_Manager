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
    <title>LaUD可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <script src="assets/js/jquery-1.10.1.js"></script>
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
                欢迎光临, <a><%
                    out.println(session.getAttribute("user"));
                    %></a>
            </p>
              <ul class="nav">
                <li class="active"><a href="./index.jsp">首页</a></li>
                <li class="divider-vertical"></li>
                <li class="dropdown">
                  <a class="dropdown-toggle" data-toggle="dropdown"> 集群管理 <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                    <li><a href="cluster/init.jsp">初始化集群</a></li>
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
                            <li><a tabindex="-1" href="#">LaUDFS</a></li>

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
              <h2>欢迎使用LaUD集群可视化管理工具!</h2>
              <p>通过此工具，您可以对LaUD集群进行一系列操作，包括初始化集群、开启关闭集群、
                  查看集群状态和日志、查看数据库信息、执行CQL查询、配置集群参数等。</p>
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="cluster/startstop.jsp">开启/关闭集群 </a></h4>
                <p>根据初始化时的配置给出集群内机器列表，并可以对列表中的机器进行开启和关闭操作。</p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="cluster/status.jsp">实时查看集群状态 </a></h4>
                <p>查看集群实时信息，包括每个机器中的线程池实时监控数据和每台机器上的Keyspace对应的实时监控数据。</p>
              </div><!--/span-->
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="cluster/init.jsp">初始化/重置集群 </a></h4>
                <p>对集群进行初始化配置，并且对token进行自动部署。</p>
              </div><!--/span-->
              <div class="span5">
                <h4><a>集群状态和实时监控 </a></h4>
                <p>查看每一台机器中的具体信息，分别为<a href="cluster/status.jsp">集群环数据</a>和<a href="cluster/monitor.jsp">实时监控数据</a></p>
              </div><!--/span-->
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="cluster/log.jsp"> 查看集群运行日志 </a></h4>
                <p>查看每一台机器上所有运行日志。 </p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="data/cql.jsp"> 执行CQL语句查询 </a></h4>
                <p>在数据库中执行CQL语句查询，并返回结果。</p>
              </div><!--/span-->
            </div>
            <div class="row">
              <div class="span5">
                <h4><a href="data/dbinfo.jsp"> 查看数据库信息 </a></h4>
                <p>查看每一个Keyspace以及其对应的ColumnFamilies的信息，同时将加入对LaUD Object和LaUDFS的支持。</p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="param/paraconfig.jsp">调整集群参数 </a></h4>
                <p>通过参数列表对集群的大部分参数进行手动调整，重启集群后生效。 </p>
              </div><!--/span-->
              <div class="span5">
                <h4><a href="param/paratuning.jsp">进行系统参数调优 </a></h4>
                <p> 通过机器学习的方法对参数进行辅助调优. </p>
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
