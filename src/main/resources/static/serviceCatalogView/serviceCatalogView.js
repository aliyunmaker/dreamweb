var servicecatalogProductVersionId;
var servicecatalogPortfolioId;
var productId;
var roleId;

$(document).ready(function(){
    $.ajax({
        url:'../userRole/getRoleId.do',
        success: function (result) {
            roleId = result.data;
            if(roleId == null) {
                $.ajax({
                    url:'../userRole/getUserRole.do',
                    success:function (result) {
                        userRole = result.data;
                        if(userRole == 'ROLE_ADMIN') {
                            alert("您还未选择角色！");
                            window.location.href = "http://localhost:8080/metadata/metadataManage.html";
                        } else {
                            alert("您还未选择角色！");
                            window.location.href = "http://localhost:8080/welcome/welcome.html";
                        }
                    }
                })
            }
        }
    })
    $.ajax({
        url: "../../userProduct/searchUserProductByUserName.do",
        success: function (result) {
            if(isTrue(result.success)) {
                var data= result.data;
                var count = data.length;
                for(var i = 0; i < count;i++) {
                    productId = data[i].id;
                    var li;
                    if(i == 0) {
                        productId = data[i].id;
                        test1(productId);
                        test2(productId);
                        li = $("<li />", {"class":"active"});
                    } else {
                        li = $("<li />", {});
                    }
                    var a = $("<a />", {
                        "href": "#basic",
                        "text": data[i].productName,
                        "data-toggle":"tab" ,
                        "style":"font-size: medium;",
                        "test":productId,
                        "click": function (e) {
                            productId = e.target.attributes.test.value;
                            test1(productId);
                            test2(productId);
                        }
                    });
                    li.append(a);
                    $('#myTab').append(li);
                    if (i == count-1) {
                        productId = data[0].id;
                    }
                }
            } else {
                alert(result.errorMsg);
            }
        }
    })
    function test1(productId) {
        $.ajax({
            url: "../../serviceCatalogView/getServicecatalogPortfolioId.do",
            data: {
                productId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    servicecatalogPortfolioId = result.data;
                } else {
                    alert(result.errorMsg);
                }
            }
        });

    }
    function test2(productId) {
        $.ajax({
            url: "../../serviceCatalogView/getApps.do",
            data: {
                productId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var appList = result.data;
                    $("#select_app").empty();
                    var option1 = "<option value='选择应用'>" + "选择应用" + "</option>";
                    $("#select_app").append(option1)
                    for (var i = 0; i < appList.length; i++){
                        var app = appList[i];
                        var option = "<option value='" + app + "'>" + app + "</option>";
                        $("#select_app").append(option);
                    }
                } else {
                    alert(result.errorMsg);
                }
            }
        });
    }
    $("#select_app").change(function () {
        $("#select_environment").empty();
        $("#select_environment").append("<option value='选择环境'>选择环境</option>");
        var select_app = $(this).val();
        $.ajax({
            url: "../../serviceCatalogView/getEnvironment.do",
            data: {
            select_app,
            productId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var environmentList = result.data;
                }
                for(var i = 0; i < environmentList.length; i++) {
                    var environment = environmentList[i];
                    var option = "<option value='" + environment + "'>" + environment + "</option>";
                    $('#select_environment').append(option);
                }
            }
        })
    })

    $("#select_environment").change(function () {
        var select_app = $("#select_app").val();
        var select_environment = $("#select_environment").val();
        $.ajax({
            url: "../../serviceCatalogView/getServicecatalogProductVersionId.do",
            data: {
            select_app,
            select_environment,
            productId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    servicecatalogProductVersionId = result.data;
               }
            }
        })
    })

    $("#btn_getExample").click(function () {
        $.ajax({
            url: "../../serviceCatalogView/getExampleName.do",
            data: {
                productId
            },
            success: function (result) {
                var provisionedProductName = result.data;
                var region = $("#select_region").val();
                $.ajax({
                    url: "../../serviceCatalogView/getNonLoginPreUrl.do",
                    data: {
                        productId,
                        roleId,
                        provisionedProductName,
                        servicecatalogProductVersionId,
                        servicecatalogPortfolioId,
                        region
                    },
                    success: function (result) {
                        if(isTrue(result.success)) {
                            var nonLoginPreUrl = result.data;
                            var iframe = '<iframe class="embed-responsive-item" id="iframe_showPreConsole" src="' + nonLoginPreUrl + '"></iframe>';
                            $("#serviceCatalogViewConsoleDiv").html(iframe);
                        }
                    }
                })
            }
        })
    })

})

window.addEventListener("message", function(event) {
    $.ajax({
        url: "../../application/processDefinitionQueryFinal.do",
        success: function (result) {
            if(isTrue(result.success)) {
                var definitionId = result.data;
                var data = JSON.parse(event.data);
                var servicecatalogPlanId = data['PlanId'];
                $.ajax({
                    url: "../../application/startPlan.do",
                    data: {
                        definitionId,
                        productId,
                        roleId,
                        servicecatalogPlanId
                    },
                    success: function (result) {
                        window.location.href = "http://localhost:8080/application/myApplication.html";
                    }
                })
            }
        }
    })
}, false)

function isTrue(isSuccess) {
    return (isSuccess === "true" || isSuccess === true);
}