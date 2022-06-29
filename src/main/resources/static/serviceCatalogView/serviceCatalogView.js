$(document).ready(function(){
    var url = location.search;
    console.log(url);
    $.ajax({
        url: "../../serviceCatalogView/getApplications.do",
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
        $("#btn_getProductId").val("无");
        var select_Application = $(this).val();
        $.ajax({
            url: "../../serviceCatalogView/getScenes.do",
            data: {
            select_Application
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
            url: "../../serviceCatalogView/getProductId.do",
            data: {
            select_Application,
            select_Scene
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var productId = result.data;
                    $("#btn_getProductId").val(productId);
               } else {
                   $("#btn_getProductId").val("您没有此产品使用权限！");
               }
            }
        })
    })

    $("#btn_getExample").click(function () {
        var productId = $("#btn_getProductId").val();
        var roleId = 1;
        $.ajax({
            url: "../../serviceCatalogView/getExampleName.do",
            data: {
                productId
            },
            success: function (result) {
                var exampleName = result.data;
                $.ajax({
                    url: "../../serviceCatalogView/getNonLoginPreUrl.do",
                    data: {
                        productId,
                        roleId,
                        exampleName
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
                var productId = $("#btn_getProductId").val();
                var PlanId = event.data;
                var roleId = 1;
                $.ajax({
                    url: "../../apply/startPlan.do",
                    data: {
                        definitionId,
                        select_Application,
                        select_Scene,
                        productId,
                        roleId,
                        PlanId
                    },
                    success: function (result) {
                        var planId = result.data;
                        alert('申请完成，请等待预检，预检ID为：' + planId);
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