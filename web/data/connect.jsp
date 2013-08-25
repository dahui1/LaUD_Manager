<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Actions.ConnectDB"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>数据库连接 &middot; LaUD可视化管理工具</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <!-- javascript -->
        <script src="../assets/js/jquery-1.10.1.js"></script>
        <script src="../assets/js/bootstrap.js"></script>
        <script src="../assets/js/functions.js"></script>
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
                if (i==select_id)
                {
                  if (document.getElementById("box"+i).style.display == "block")
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
            var url = location.search; //获取url中"?"符后的字串
            if (url.indexOf("?") !== -1) {
                alert ("输入的IP不在集群中，您可以查看\"集群管理->集群状态\"中显示的机器，并选择其中任意一个重新输入！");
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
                        <li class="active" id="bs">数据库连接</li>
                    </ul>
                </div>
                <div class="row">
                    <div class="span8 offset2" id="unconnected">
                        您尚未与数据库建立连接，请输入需要连接的数据库对应的IP和端口号：
                        <form method ="post" action="ConnectDB.action">
                            <table>
                                <tbody id="addresses">
                                    <tr>
                                        <td> <input type="text" class="input-medium" placeholder="IP" name="connectip" value="192.168.3.1">
                                            <input type="text" class="input-mini" placeholder="Port" name="connectport" value="9170">
                                        </td>
                                    </tr>
                            </table>
                            <!--  <i class="icon-plus" onclick="addEntry(2)"></i>-->
                            <button type="submit" class="btn btn-primary">连接数据库</button>
                        </form>
                    </div>
                    <div class="span8 offset2" id="connected" style="display:none">
                    </div>
                    <script>
                        var connected = "<%=(String)session.getAttribute("connected")%>";
                        if  (connected!=="null"){ 
                        
                            document.getElementById("unconnected").style.display = "none";
                            document.getElementById("connected").style.display = "block";
                            document.getElementById("connected").innerHTML = "您和数据库已连接，连接地址为" + connected
                                    + "<br><br><button class=\"btn btn-primary\" onclick=\"disconnect()\">断开连接</button>";
                        }
                    </script>
                </div>
            </div>
        <div id="push"></div>

        </div>

        <div id="footer">
            <div class="container">
                <p class="muted credit" align="middle">Developed by LaUD</p>
            </div>
        </div>
    </body>
</html>