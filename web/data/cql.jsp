<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <title>CQL查询 &middot; LaUD可视化管理工具</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">
        <!-- javascript -->
        <script type="text/javascript" src="../assets/js/jquery-1.10.1.js"></script>
        <script type="text/javascript" src="../assets/js/bootstrap.js"></script>
        <script type="text/javascript" src="../assets/js/bootstrap-transition.js"></script>
        <script type="text/javascript" src="../assets/js/bootstrap-tooltip.js"></script>
        <script type="text/javascript" src="../assets/js/bootstrap-popover.js"></script>
        <script src="../assets/js/cql.js"></script>
        <!-- CSS -->
        <link href="../assets/css/bootstrap.css" rel="stylesheet">
        <link href="../assets/css/footer.css" rel="stylesheet">
        <link href="../assets/css/bootstrap-datetimepicker.min.css" rel="stylesheet" type="text/css" media="screen">
        <style type="text/css">
            textarea
            {
            width:95%;
            height:100%;
            }
        </style>
        <link href="../assets/css/bootstrap-responsive.css" rel="stylesheet">
        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
        <script src="../assets/js/html5shiv.js"></script>
        <![endif]-->
        <style type="text/css">
            div.cqlrs 
            {
            width: 250px;      
            height:500px;
            overflow-y: auto;
            }
        </style>
    </head>
    <body>
        <script>
            var user = <%=(String)session.getAttribute("user")%>;
            var connected = "<%=(String)session.getAttribute("connected")%>";
            if (user === null) {
                alert("请先登录！");
                window.location = "../signin.jsp";
            }
            
            else if (connected === "null") {
              alert("请先连接数据库，再进行查询操作！");
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
                        <li class="active">执行CQL查询</li>
                    </ul>
                </div>
                <div class="row">
                    <div class="span3 accordion" id="kscf">
                    </div>
                    <div class="span9" name="cqlinput">
                        <legend>请输入CQL语句</legend>
                        Keyspace和其对应的Column Families的定义可参考左侧<br>       
                        <label>
                        <textarea rows="7" id="cqlstring" ></textarea>
                        </label> <br>
                        <p>
                            <button class="btn btn-primary pull-right" type="button" onclick="cqlexec('firstpage');">执行</button>
                        </p>
                        <br>
                        <hr>
                        <!--a id="li1" onclick="li1()" >>>></a-->
                        <p id="errorinfo"></p>
                        <div  id="cqlrsdiv">
                            <!--table class="table table-hover">
                                <thead>
                                    <tr align="left">
                                        <th>  </th>
                                        <th>name</th>
                                        <th>value</th>
                                        <th>  </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>1</td>
                                        <td>apple</td>
                                        <td>apple</td>
                                        <td><a id="li1"  rel="popover" >>>></a></td>
                                    </tr>
                                </tbody>
                                </table-->
                        </div>
                        <div class="pagination pagination-right" id="paginationdiv" style="display:none">
                            <ul style="margin-right:30px;">
                                <li id="previouspage"><a  onclick="cqlexec('previous');"><</a></li>
                                <li id="nextpage"><a  onclick="cqlexec('next');">></a></li>
                            </ul>
                            <p>第<i id="currentpage"> </i>页(共<i id="totalpage"> </i>页)
                                <input id="gotopage" type="text" class="input-mini" placeholder="go to">
                                <a  onclick="cqlexec('goto');">GO</a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <div id="push"></div>

        </div>
        </div>
        <div id="footer">
            <div class="container">
                <p class="muted credit" align="middle">Developed by LaUD</p>
            </div>
        </div>
    </body>
</html>