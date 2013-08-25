<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>参数调优 &middot; LaUD可视化管理工具</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <!-- javascript -->
        <script src="../assets/js/jquery-1.10.1.js"></script>
        <script src="../assets/js/bootstrap.js"></script>
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
                document.getElementById("param").className = "dropdown active"; 
            </script>
            <!-- Begin page content -->
            <div class="container">
                <div id="bs">
                    <ul class="breadcrumb">
                        <li> <span class="divider">/</span></li>
                        <li>参数管理<span class="divider">/</span></li>
                        <li class="active">参数调优</li>
                    </ul>
                </div>
            </div>
        </div>
        <div id="push"></div>

        <div id="footer">
            <div class="container">
                <p class="muted credit" align="middle">Developed by LaUD</a>.</p>
            </div>
        </div>
    </body>
</html>