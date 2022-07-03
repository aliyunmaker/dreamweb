var search = window.location.search.substring(1);
var urlsp = new URLSearchParams(search);
var productId = urlsp.get("productId");
var productName = urlsp.get("productName");
var productVersionId;
var portfolioId;

$(document).ready(function(){

    $.ajax({
        url: "../../serviceCatalogView/getPortfolioId.do",
        data: {
            productId
        },
        success: function (result) {
            if(isTrue(result.success)) {
                portfolioId = result.data;
            } else {
                alert(result.errorMsg);
            }
        }
    });

    $.ajax({
        url: "../../serviceCatalogView/getApplications.do",
        data: {
            productId
        },
        success: function (result) {
            if(isTrue(result.success)) {
                var applicationList = result.data;
                for (var i = 0; i < applicationList.length; i++){
                    var application = applicationList[i];
                    var option = "<option value='" + application + "'>" + application + "</option>";
                    $("#select_application").append(option);
                }
            } else {
                alert(result.errorMsg);
            }
        }
    });

    $("#select_application").change(function () {
        $("#select_scenes").empty();
        $("#select_scenes").append("<option value='选择环境'>选择环境</option>");
        var select_Application = $(this).val();
        $.ajax({
            url: "../../serviceCatalogView/getScenes.do",
            data: {
            select_Application,
            productId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var scenesList = result.data;
                }
                for(var i = 0; i < scenesList.length; i++) {
                    var scene = scenesList[i];
                    var option = "<option value='" + scene + "'>" + scene + "</option>";
                    $('#select_scenes').append(option);
                }
            }
        })
    })

    $("#select_scenes").change(function () {
        var select_Application = $("#select_application").val();
        var select_Scene = $("#select_scenes").val();
        $.ajax({
            url: "../../serviceCatalogView/getProductVersionId.do",
            data: {
            select_Application,
            select_Scene,
            productId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    productVersionId = result.data;
               }
            }
        })
    })

    $("#btn_getExample").click(function () {
        var roleId = 1;
        $.ajax({
            url: "../../serviceCatalogView/getExampleName.do",
            data: {
                productId
            },
            success: function (result) {
                var exampleName = result.data;
                var region = $("#select_region").val();
                $.ajax({
                    url: "../../serviceCatalogView/getNonLoginPreUrl.do",
                    data: {
                        productId,
                        roleId,
                        exampleName,
                        productVersionId,
                        portfolioId,
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
        url: "../../apply/processDefinitionQueryFinal.do",
        success: function (result) {
            if(isTrue(result.success)) {
                var definitionId = result.data;
                var select_Application = $("#select_application").val();
                var select_Scene = $("#select_scenes").val();
                var PlanId = event.data;
                console.log(event.data);
                var roleId = 1;
                $.ajax({
                    url: "../../apply/startPlan.do",
                    data: {
                        definitionId,
                        select_Application,
                        select_Scene,
                        productId,
                        roleId,
                        PlanId,
                        portfolioId,
                        productName
                    },
                    success: function (result) {
                        var planId = result.data;
                        alert('已提交，请等待预检，计划ID为：' + planId);
                        window.location.href = "http://localhost:8080/ask/myAsk.html";
                    }
                })
            }
        }
    })
}, false)

function isTrue(isSuccess) {
    return (isSuccess === "true" || isSuccess === true);
}