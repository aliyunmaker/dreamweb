var search = window.location.search.substring(1);
var urlsp = new URLSearchParams(search);
var productId = urlsp.get("productId");
var productName = urlsp.get("productName");
console.log(productId);
console.log(productName);
var servicecatalogProductVersionId;
var servicecatalogPortfolioId;
var roleId = urlsp.get("roleId");

$(document).ready(function(){
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

    $.ajax({
        url: "../../serviceCatalogView/getApps.do",
        data: {
            productId
        },
        success: function (result) {
            if(isTrue(result.success)) {
                var appList = result.data;
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
                console.log(result);
                if(isTrue(result.success)) {
                    var environmentList = result.data;
                    console.log(environmentList);
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
                    console.log(result.data);
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
                console.log(provisionedProductName);
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
                            var iframe = '<iframe style="height:100%;" class="embed-responsive-item" id="iframe_showPreConsole" src="' + nonLoginPreUrl + '"></iframe>';
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