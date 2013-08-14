<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>集群初始化 &middot; LaUD可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery-1.10.1.js"></script>
    <script src="../assets/js/bootstrap.js"></script>
    <script src="../assets/js/init.js"></script>

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
          <li id="all" class="active">初始化集群</li>
          <li id="one" style="display:none"></li>
        </ul>
      </div>
      <div class="span8 offset2" id="params">
        <fieldset  align="center">
          <legend align="left">请输入集群各初始化参数：</legend>
          <div class="form-group">
            <label for="inputRoot" align="left">LaUD根目录</label>
            <input type="text" class="form-control input-xxlarge" name="rootdir" id="inputRoot" placeholder="Enter the Root Directory">
          </div>
          <div class="form-group">
            <label for="inputName" align="left">集群名称（Cluster Name）</label>
            <input type="text" class="form-control input-xxlarge" name="clustername" id="inputName" placeholder="Enter Cluster Name">
          </div>
          <div class="form-group">
            <label for="inputDataDir" align="left">数据文件目录（Data File Directories）</label>
            <input type="text" class="form-control input-xxlarge" name="datafiledir" id="inputDataDir" placeholder="Enter data file directories">
            <span class="help-block">Use ";" to separate different data file directories.</span>
          </div>
          <div class="form-group">
            <label for="inputCommitDir" align="left">提交日志目录（Commitlog Directory）</label>
            <input type="text" class="form-control input-xxlarge" name="commitlogdir" id="inputCommitDir" placeholder="Enter Commitlog Directory">
          </div>
          <div class="form-group">
            <label for="inputCacheDir" align="left">缓存存储目录（Saved Caches Directory）</label>
            <input type="text" class="form-control input-xxlarge" name="savedcachesdir" id="inputCacheDir" placeholder="Enter Saved Caches Directory">
          </div>
          <div class="form-group">
            <label for="inputRpcPort" align="left">RPC端口号（RPC Port）</label>
            <input type="text" class="form-control input-xxlarge" name="rpcport" id="inputRpcPort" placeholder="Enter RPC Port">
          </div>           
          <div class="form-group">
              <label for="inputSeeds" align="left">初始化种子（Seeds and Tokens）</label>
              <div  align="left">
                &nbsp;&nbsp;&nbsp; 请选择生成方法：<select id="tokenMethod"class="form-control" onchange="showTable()">
                <option>随机生成Token</option>
                <option>指定Token</option>
              </select></br>
              &nbsp;&nbsp;&nbsp;&nbsp;请输入种子数目：
              <input type="number" min="1" class="form-control" id="inputSeedCount" onchange="showTable()">
              <table id="seeds" align="center">

              </table>
              </div>
          </div>
          <button class="btn btn-primary" onclick="submitInitParam()" >提交并初始化（重置）系统</button>
          <br>
        </fieldset>
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
