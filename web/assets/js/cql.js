$(function () {
    $(document).ready(function () {
        $.ajax({
            url: 'GetKSCFs',
            type: 'POST',
            data: "{}",
            dataType: 'json',
            success: function (data) {
                $("#kscf").append("<h4>Keyspaces</h4>");
                $(data).each(function (i, value) {
                    $("#kscf").append("<div class=\"accordion-group\"><div class=\"accordion-heading\">" +
                        "<a class=\"accordion-toggle\" data-toggle=\"collapse\" data-parent='#kscf' href='#ks" + i + "'>" +
                        value[0] + "</a></div><div id='ks" + i + "'class='accordion-body collapse'>" +
                        "<div class=\"accordion-inner\" id=\"ul" + i + "\"></div></div>");
                    for (var j = 1; j < value.length; j++) {
                        $("#ul" + i).append("<p>" + value[j] + "</p>");
                    }
                });
                $("#kscf").append("</div>");
            }
        });
    });
});

function cqlexec(type) {
    var cqlstring = $("#cqlstring").val();
    var gotopage = $("#gotopage").val();
    var typestring = type;
    var current = document.getElementById("currentpage").innerHTML;
    var total = document.getElementById("totalpage").innerHTML;
    if ((current === "1" || current === "0") && (typestring === "previous")) {
        return;
    }
    if (current === total && typestring === "next") {
        return;
    }
    if (typestring === "goto") {
        var reg = /\d+/;
        if (!reg.test(gotopage)) {
            alert("请输入数字！");
            return;
        }
        if (Number(gotopage) < 1 || Number(gotopage) > Number(total)) {
            alert("请输入正确的页码！");
            return;
        }
    }
    $.ajax({
        url: 'CQLexec.action',
        type: 'POST',
        data: {
            "cqlstring": cqlstring,
            "typestring": typestring,
            "current": current,
            "total": total,
            "gotopage": gotopage
        },
        dataType: 'json',
        success: function (data) {
            document.getElementById("cqlrsdiv").innerHTML = "";
            document.getElementById("errorinfo").innerHTML = "";
            document.getElementById("paginationdiv").style.display = "none";
            var isnull = data.rslist;
            var err = data.errorinfo;
            if (err === null) {
                if (isnull !== null) {

                    if (typestring === "firstpage") {
                        document.getElementById("totalpage").innerHTML = data.totalpage;
                        if (data.totalpage === 0) {
                            document.getElementById("currentpage").innerHTML = 0;

                        } else if (data.totalpage === 1) {
                            document.getElementById("currentpage").innerHTML = data.currentpage;

                        } else {
                            document.getElementById("currentpage").innerHTML = data.currentpage;
                        }
                    }
                    if (typestring === "next" || typestring === "previous" || typestring === "goto") {
                        document.getElementById("currentpage").innerHTML = data.currentpage;
                    }
                    var obj = document.getElementById("cqlrsdiv");
                    var str = "";
                    var oScript = document.createElement("script");

                    $("#cqlrsdiv").append(data.thead);
                    $(data.rslist).each(function (i, value) {
                        $("#rstable").append(value);
                    });
                    $(data.jslist).each(function (i, value) {
                        str += (value + "\n");
                    });
                    oScript.text = str;
                    obj.appendChild(oScript);
                    document.getElementById("paginationdiv").style.display = "block";
                } else {
                    document.getElementById("errorinfo").innerHTML = "CQL EXECUTE SUCCESSFULLY";
                }
            } else {
                document.getElementById("errorinfo").innerHTML = data.errorinfo;
            }
        }
    });
}