<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>系统日志 &middot; LaUD可视化管理工具</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <!-- Javascript -->
        <script type="text/javascript" src="../assets/js/jquery-1.10.1.js"></script>
        <script type="text/javascript" src="../assets/js/bootstrap.js"></script>
        <script type="text/javascript" src="../assets/js/jquery.js"></script>
        <script type="text/javascript" src="../assets/js/log.js"></script>
        <script type="text/javascript" src="../assets/js/bootstrap-modal.js"></script>
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
                        <li class="active">日志查看</li>
                    </ul>
                </div>
                <div class="span8 offset2">
                    <input id="logpath" type="text" placeholder ="请输入日志路径" value="/data1/logcassandra/logs/"/><br>
                    <input id="logip" type="text" placeholder="请输入ip地址"/>
                    <p>
                        <button class="btn btn-primary pull-right" type="button" onclick="showloglist();">获取日志列表</button>
                    </p>
                    <br>
                    <hr>
                    <table class="table table-hover" id="logtable" >
                        <tbody id="logs">
                        </tbody>
                    </table>
                </div>
                <div id="logmodal" class="modal hide fade" tabindex="-1" aria-hidden="true">
                    <div class="modal-header" >
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                        <h3 id="logfilename"></h3>
                    </div>
                    <div class="modal-body" id="logmodalbody">
                    </div>
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