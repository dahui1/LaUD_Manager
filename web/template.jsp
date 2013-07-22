  <%@page contentType="text/html" pageEncoding="UTF-8"%>

        <div class="navbar navbar-fixed-top">
          <div class="navbar-inner">
            <div class="container">
              <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
              </button>
              <a class="brand" href="../index.jsp">LaUD Manager</a>
              <div class="nav-collapse collapse">
              <p class="navbar-text pull-right">
                  欢迎光临, <%
                      out.println(session.getAttribute("user"));
                      %>
              </p>
                <ul class="nav">
                  <li id="indexpage"><a href="../index.jsp">首页</a></li>
                  <li class="divider-vertical"></li>
                  <li class="dropdown" id="cluster">
                    <a class="dropdown-toggle" data-toggle="dropdown"> 集群管理 <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                      <li><a href="../cluster/startstop.jsp"> 启动/关闭集群 </a></li>
                      <li><a href="../cluster/status.jsp"> 集群状态 </a></li>
                      <li><a href="../cluster/monitor.jsp"> 集群监控 </a></li>
                      <li><a href="../cluster/log.jsp"> 日志查看 </a></li>
                    </ul>
                  </li>
                  <li class="divider-vertical"></li>
                  <li class="dropdown"  id="data">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"> 数据管理 <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                      <li><a href="../data/cql.jsp">CQL查询</a></li>
                      <li><a href="../data/connect.jsp"> 连接数据库</a></li>
                      <li class="dropdown-submenu">
                          <a tabindex="-1" href="#">数据库信息</a>
                          <ul class="dropdown-menu pull-right">
                              <li><a tabindex="-1" href="../data/dbinfo.jsp">LaUD KV</a></li>
                              <li><a tabindex="-1" href="#">LaUD Object</a></li>
                              <li><a tabindex="-1" href="#">LaUD ..</a></li>
                          </ul>
                      </li>
                    </ul>
                  </li>
                  <li class="divider-vertical"></li>
                  <li class="dropdown" id="param">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"> 参数管理 <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                      <li><a href="../param/paraconfig.jsp"> 参数配置 </a></li>
                      <li><a href="../param/paratuning.jsp"> 参数调优 </a></li>
                    </ul>
                  </li>
                  <li class="divider-vertical"></li>
                </ul>
              </div>
            </div>
          </div>
        </div>

