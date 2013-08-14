<%@page language="java" import="java.util.*" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<%@page import="Actions.SigninAction"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Sign in &middot; LaUD Management Tool</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="assets/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 40px;
        padding-bottom: 40px;
        background-color: #f5f5f5;
      }

      .form-signin {
        max-width: 300px;
        padding: 19px 29px 29px;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
           -moz-border-radius: 5px;
                border-radius: 5px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
      }
      .form-signin input[type="text"],
      .form-signin input[type="password"],
      .form-signin input[type="number"] {
        font-size: 16px;
        height: auto;
        margin-bottom: 15px;
        padding: 7px 9px;
      }
      
    </style>
    <link href="assets/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="../assets/js/html5shiv.js"></script>
    <![endif]-->

  </head>

  <body>
      <script>
          var user = <%=(String)session.getAttribute("user")%>;
          if (user !== null) {
              window.location = "index.jsp";
          }
          var url = location.search; //获取url中"?"符后的字串
          if (url.indexOf("?") !== -1) {
              alert ("用户名或密码输入错误！请重试或联系管理员！");
          }
          
          function showorhide() {
              if (document.getElementById("arrow").className == "icon-arrow-down") {
                  document.getElementById("input_ip").style.display = "block";
                  document.getElementById("input_tp").style.display = "block";
                  document.getElementById("input_jp").style.display = "block";
                  document.getElementById("input_root").style.display = "block";
                  document.getElementById("tip").innerHTML = "Click to hide the inputs<i class=\"icon-arrow-up\" id=\"arrow\"></i>";
              }
              else {
                  document.getElementById("input_ip").style.display = "none";
                  document.getElementById("input_tp").style.display = "none";
                  document.getElementById("input_jp").style.display = "none";
                  document.getElementById("input_root").style.display = "none";
                  document.getElementById("tip").innerHTML = "Click to edit IP/Ports<i class=\"icon-arrow-down\" id=\"arrow\"></i>";
              }
          }
      </script>
    <div class="container">

        <form class="form-signin" method="post" action="Signin.action">
        <h3 class="form-signin-heading">Please sign in</h3>
        <input name="username" type="text" class="input-block-level" placeholder="User"/>
        <input name="password" type="password" class="input-block-level" placeholder="Password"/>
        <input id="input_ip" name="ip" type="text" class="input-block-level" placeholder="IP Address" value="192.168.3.1" style="display:none"/>
        <input id="input_tp" name="thrift" type="number" height="24px" class="input-block-level" placeholder="Thrift Port" value="9170" style="display:none"/>
        <input id="input_jp" name="jmx" type="number" class="input-block-level" placeholder="JMX Port" value="7199" style="display:none"/>
        <input id="input_root" name="root" type="text" class="input-block-level" placeholder="LaUD Directory" value="/home/usdms/laud-1.0.7/" style="display:none"/>
        <div align="right" class="inline" onclick="showorhide()" id="tip">Click to edit IP/Ports<i class="icon-arrow-down" id="arrow"></i></div>
        <!--<label class="checkbox">
          <input type="checkbox" value="remember-me"> Remember me
        </label> -->
        <div id="alertDiv"><p></p></div>

        <button class="btn btn-primary" type="submit">Sign in </button>
      </form>

    </div> <!-- /container -->

        <p class="muted credit" align="middle">Developed by LaUD</a>.</p>


    <!-- javascript -->
    <script src="assets/js/jquery.js"></script>
    <script src="assets/js/bootstrap.js"></script>
    <script src="assets/js/sha1.js"></script>
  </body>
</html>
