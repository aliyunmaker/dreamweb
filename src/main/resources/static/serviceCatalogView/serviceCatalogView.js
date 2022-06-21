$(document).ready(function(){
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
        $("#select_scenes").append("<option value='选择场景'>选择场景</option>");
        $("#btn_getProductId").empty();
        $("#btn_getProductId").append("<option value='查询产品id'>无</option>");
        $("#btn_getExample").empty();
        $("#btn_getExample").append("<option value='请选择以上参数'>请选择以上参数</option>");
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
        $("#btn_getProductId").empty();
        $("#btn_getExample").empty();
        $("#btn_getExample").append("<option value='创建实例名称'>生成实例名称并进入下一步</option>");

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
                    var option = "<option value='" + productId + "'>" + productId + "</option>";
                    $("#btn_getProductId").append(option);
               } else {
                   var option = "<option value='未找到对应product！'> 您没有此产品使用权限！</option>";
                   $("#btn_getProductId").append(option);
               }
            }
        })
    })

    $("#btn_getExample").click(function () {
        var productId = $("#btn_getProductId").text();
        var roleId = 1;

        $("#btn_getExample").empty();
        $.ajax({
            url: "../../serviceCatalogView/getExampleName.do",
            data: {
                productId
            },
            success: function (result) {
                var exampleName = result.data;
                var option = "<option value='" + exampleName + "'>" + exampleName + "</option>";
                $("#btn_getExample").append(option);
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
        url: "../../apply/processDefinitionQueryFinal.do",
        success: function (result) {
            if(isTrue(result.success)) {
                var definitionId = result.data;
                var select_Application = $("#select_application").val();
                var select_Scene = $("#select_scenes").val();
                var productId = $("#btn_getProductId").text();
                var exampleName = $("#btn_getExample").text();
                var parameter = event.data;
                var roleId = 1;
                    $.ajax({
                        url: "../../apply/startProcessByDefinitionId.do",
                        data: {
                            definitionId,
                            select_Application,
                            select_Scene,
                            productId,
                            exampleName,
                            roleId,
                            parameter
                        },
                        success: function (result) {
                            var processInstanceId = result.data;
                            alert('申请完成，请等待审批，审批ID为：' + processInstanceId + '!');
                            window.location.href = "http://localhost:8080/ask/myAsk.html";
                        }
                    })
                
                // })
            }
        }
    })
}, false)

function isTrue(isSuccess) {
    return (isSuccess === "true" || isSuccess === true);
}