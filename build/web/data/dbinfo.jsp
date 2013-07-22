<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>数据库信息 &middot; Cassandra可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery.js"></script>
    <script src="../assets/js/bootstrap.js"></script>

    <!-- CSS -->
    <link href="../assets/css/bootstrap.css" rel="stylesheet">
   <style type="text/css">
      html,
      body {
        height: 100%;
      }

      #wrap {
        min-height: 100%;
        height: auto !important;
        height: 100%;
        margin: 0 auto -60px;
      }

      #push,
      #footer {
        height: 60px;
      }
      #footer {
        background-color: #f5f5f5;
      }

      @media (max-width: 767px) {
        #footer {
          margin-left: -20px;
          margin-right: -20px;
          padding-left: 20px;
          padding-right: 20px;
        }
      }

      #wrap > .container {
        padding-top: 60px;
      }
      .container .credit {
        margin: 20px 0;
      }

      code {
        font-size: 80%;
      }

    </style>
    <script>
    function openP(_id, count)
    {
      var select_id = parseInt(_id.replace("box",""));
      for (i=1;i<=count;i++)
      {
        if (i===select_id)
        {
          if (document.getElementById("box"+i).style.display === "block")
          {
            document.getElementById("box"+i).style.display = "none";
            break;
          }
          document.getElementById("box"+i).style.display = "block";
        }
        else
        {
          document.getElementById("box"+i).style.display = "none";
        }
      }
    }
    </script>
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
              window.location = "../signin.jsp";
          }
          var connected = <%=(String)session.getAttribute("connected")%>;
          if (connected === null) {
            alert("请先连接数据库，再查看数据库信息！");
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
          <li>数据库信息<span class="divider">/</span></li>
          <li class="active" id="bs">Keyspace1</li>
        </ul>
      </div>
      <div class="row">
        <div class="span2">
          <ul class="nav nav-list">
            <li class="nav-header">Keyspaces</li>
            <li class="active" id="nav1"><a href="#">Keyspace1</a></li>
            <li id="nav2"><a href="#">Keyspace2</a></li>
            <li id="nav3"><a href="#">Keyspace3</a></li>
            <li id="nav4"><a href="#">Keyspace4</a></li>
            <li id="nav5"><a href="#">Keyspace5</a></li>
          </ul>
        </div>
        <div class="span10" id="ks1">
          <h5>Keyspace Settings</h5>
          <table class="table table-bordered">
            <tbody>
              <tr>
                <td>replica_placement_strategy</td>
                <td>org.apache.cassandra.locator.SimpleStrategy</td>
              </tr>
              <tr>
                <td>replication_factor</td>
                <td>1</td>
              </tr>
            </tbody>
          </table>
          <hr>
          <h5>Column Families</h5>
            <ul class="nav nav-pills nav-stacked">
              <li><a href="#" onclick="openP('box1',4)">CF1</a></li>
              <div id="box1" style="display:none">
              这里面放的是CF1相关的内容.
              </div>
              <li><a href="#" onclick="openP('box2',4)">CF2</a></li>
              <div id="box2" style="display:none">
              这里面放的是CF2相关的内容.
              </div>
              <li><a href="#" onclick="openP('box3',4)">CF3</a></li>
              <div id="box3" style="display:none">
              这里面放的是CF3相关的内容.
              </div>
              <li><a href="#" onclick="openP('box4',4)">CF4</a></li>
              <div id="box4" style="display:none">
              这里面放的是CF4相关的内容.
              </div>
            </ul>
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