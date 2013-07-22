<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>参数配置 &middot; Cassandra可视化管理工具</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <!-- javascript -->
    <script src="../assets/js/jquery.js"></script>
    <script src="../assets/js/bootstrap.js"></script>
    <script src="../assets/js/bootstrap-editable.min.js"></script>
    <!-- CSS -->
    <link href="../assets/css/bootstrap.css" rel="stylesheet">
    <link href="../assets/css/bootstrap-editable.css" rel="stylesheet"/>
    <link href="../assets/css/footer.css" rel="stylesheet">
    <link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">

    <script type="text/javascript">
    function openTable(_id, count)
    {
      var select_id = parseInt(_id.replace("box",""));
      if (document.getElementById(_id).style.display == "block")
      {
        return;
      }
      for (i=1;i<=count;i++)
      {
        if (i==select_id)
        {
          document.getElementById("box"+i).style.display = "block";
          document.getElementById("nav"+i).className = "active";
          document.getElementById("bs").innerHTML = document.getElementById("nav"+i).innerHTML;
        }
        else
        {
          document.getElementById("box"+i).style.display = "none";
          document.getElementById("nav"+i).className = "none";
        }
      }
    }
    </script>

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
      <div>
        <ul class="breadcrumb">
          <li> <span class="divider">/</span></li>
          <li>参数管理<span class="divider">/</span></li>
          <li>参数配置<span class="divider">/</span></li>
          <li class="active" id="bs">启动参数</li>
        </ul>
      </div>
      <div class="row">
        <div class="span2">
          <ul class="nav nav-list">
            <li class="nav-header">参数类型</li>
            <li class="active"><a href="#" onclick="openTable('box1',2)" id="nav1">启动参数</a></li>
            <li><a href="#" onclick="openTable('box2',2)" id="nav2">运行参数</a></li>
          </ul>
        </div>
        <div class="span10" id="box1">
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>参数名</td>
                <td>默认值</td>
                <td>当前值</td>
                <td>变化范围</td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>最大消息长度</td>
                <td>16</td>
                <td><a href="#" id="para1">16</a></td>
                <script type="text/javascript">
                  $('#para1').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>结构化传输包大小</td>
                <td>15</td>
                <td><a href="#" id="para2">15</a></td>
                <script type="text/javascript">
                  $('#para2').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>列索引大小</td>
                <td>64</td>
                <td><a href="#" id="para3">64</a></td>
                <script type="text/javascript">
                  $('#para3').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,256]</td>
              </tr>
              <tr>
                <td>远程过程调用时间限制</td>
                <td>10000</td>
                <td><a href="#" id="para4">10000</a></td>
                <script type="text/javascript">
                  $('#para4').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,100000]</td>
              </tr>
              <tr>
                <td>提交阈值</td>
                <td>8</td>
                <td><a href="#" id="para5">8</a></td>
                <script type="text/javascript">
                  $('#para5').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,11]</td>
              </tr>
              <tr>
                <td>并发读操作数</td>
                <td>32</td>
                <td><a href="#" id="para6">32</a></td>
                <script type="text/javascript">
                  $('#para6').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>并发写操作数</td>
                <td>32</td>
                <td><a href="#" id="para7">32</a></td>
                <script type="text/javascript">
                  $('#para7').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>并发备份数</td>
                <td>/</td>
                <td><a href="#" id="para8">16</a></td>
                <script type="text/javascript">
                  $('#para8').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>刷新写操作数</td>
                <td>1</td>
                <td><a href="#" id="para9">1</a></td>
                <script type="text/javascript">
                  $('#para9').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>内存压缩限制</td>
                <td>64</td>
                <td><a href="#" id="para10">64</a></td>
                <script type="text/javascript">
                  $('#para10').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,256]</td>
              </tr>
              <tr>
                <td>并发压缩数</td>
                <td>1</td>
                <td><a href="#" id="para11">1</a></td>
                <script type="text/javascript">
                  $('#para11').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>是否支持多线程压缩</td>
                <td>是</td>
                <td><a href="#" id="para12">是</a></td>
                <script type="text/javascript">
                  $('#para12').editable({
                  type: 'select',
                  url: '/post',
                  title: '选择新值',
                  value: 1,    
                  source: [
                        {value: 1, text: '是'},
                        {value: 2, text: '否'},
                     ]
                  });
                </script>
                <td>是，否</td>
              </tr>
              <tr>
                <td>每秒压缩吞吐量</td>
                <td>16</td>
                <td><a href="#" id="para13">16</a></td>
                <script type="text/javascript">
                  $('#para13').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>每秒上界流吞吐量</td>
                <td>400</td>
                <td><a href="#" id="para14">400</a></td>
                <script type="text/javascript">
                  $('#para14').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,800]</td>
              </tr>
              <tr>
                <td>远程调用最小线程数</td>
                <td>16</td>
                <td><a href="#" id="para15">16</a></td>
                <script type="text/javascript">
                  $('#para15').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,50]</td>
              </tr>
              <tr>
                <td>远程调用最大线程数</td>
                <td>2048</td>
                <td><a href="#" id="para16">2048</a></td>
                <script type="text/javascript">
                  $('#para16').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,5000]</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="span10" id="box2" style="display:none">
          <table class="table table-bordered">
            <thead>
              <tr>
                <td>参数名</td>
                <td>默认值</td>
                <td>当前值</td>
                <td>变化范围</td>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>最大消息长度</td>
                <td>16</td>
                <td><a href="#" id="para17">16</a></td>
                <script type="text/javascript">
                  $('#para17').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
              <tr>
                <td>结构化传输包大小</td>
                <td>15</td>
                <td><a href="#" id="para18">15</a></td>
                <script type="text/javascript">
                  $('#para18').editable({
                  type: 'text',
                  url: '/post',
                  title: '输入新值'
                  });
                </script>
                <td>(0,64]</td>
              </tr>
            </table>
          </div>
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
