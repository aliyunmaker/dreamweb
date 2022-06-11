$(document).ready(function(){
    $.ajax({
            url: "../../preView/getRoles.do",
            success: function (result) {
                if(isTrue(result.success)) {
                    var roleList = result.data;
                    for (var i = 0; i < roleList.length; i++) {
                      var role = roleList[i];
                       var roleJson = '{"roleName":"' + role.roleName + '","id":"' + role.id + '"}';
                       var option = "<option value='" + roleJson + "'>" + role.roleName + "</option>";
                       $("#select_role").append(option);
                   }
                } else {
                    alert(result.errorMsg);
                }
            }
        });

    $.ajax({
        url: "../../preView/getProduct.do",
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

    $.ajax({
        url: "../../apply/processDefinitionQueryFinal.do",
        success: function (result) {
            if(isTrue(result.success)) {
                var definitionId = result.data;
                $("#btn_startProcess").click(function(){
                    $("#btn_startProcess").empty();
                    var select_Application = $("#select_application").val();
                    var select_Scene = $("#select_scenes").val();
                    var productId = $("#btn_getProductId").text();
                    var exampleName = $("#btn_getExample").text();
                    var selectedRole = JSON.parse($("#select_role").val());
                    var roleId = selectedRole.id;
                    $.ajax({
                        url: "../../apply/startProcessByDefinitionId.do",
                        data: {
                            definitionId,
                            select_Application,
                            select_Scene,
                            productId,
                            exampleName,
                            roleId
                        },
                        success: function (result) {
                            var processInstanceId = result.data;
                            $("#btn_startProcess").append("<option value='" + processInstanceId + "'>" + "审批已提交，实例ID为：" + processInstanceId + "</option>");
                        }
                    })
                })
            }
        }
    })

    $("#select_role").change(function () {
        var selectedRole = JSON.parse($(this).val());
        var roleId = selectedRole.id;
        $.ajax({
            url: "../../preView/listProductsAsEndUser.do",
            data: {
                roleId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var productIdList = result.data;
                }
                for(var i = 0; i < productIdList.length; i++) {
                    var productId = productIdList[i];
                    console.log(productId);
                }
            }
        })
    })


    $("#select_application").change(function () {
        $("#select_scenes").empty();
        $("#select_scenes").append("<option value='选择场景'>选择场景</option>");
        $("#btn_getProductId").empty();
        $("#btn_getProductId").append("<option value='查询产品id'>查询产品id</option>");

        var select_Application = $(this).val();


        $.ajax({
            url: "../../preView/getScenes.do",
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
        $("#btn_getProductId").append("<option value='查询产品id'>查询产品id</option>");
    })

    $("#btn_getProductId").click(function () {

        $("#btn_getProductId").empty();
        $("#btn_getExample").empty();
        $("#btn_getExample").append("<option value='创建实例及实例名称'>创建实例及实例名称</option>");
        // $("#btn_getProductId").append("<option value='产品id为：'>产品id为：</option>");

        var select_Application = $("#select_application").val();
        var select_Scene = $("#select_scenes").val();

        $.ajax({
            url: "../../preView/getProductId.do",
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
                   var option = "<option value='未找到对应product！'> 未找到对应product！</option>";
                   $("#btn_getProductId").append(option);
               }
            }
        })
        
    })

    $("#btn_getExample").click(function () {
        var productId = $("#btn_getProductId").text();
        var selectedRole = JSON.parse($("#select_role").val());
        var roleId = selectedRole.id;

        $("#btn_getExample").empty();
        $.ajax({
            url: "../../preView/getExampleName.do",
            data: {
                productId
            },
            success: function (result) {
                var exampleName = result.data;
                var option = "<option value='" + exampleName + "'>" + exampleName + "</option>";
                $("#btn_getExample").append(option);
                $.ajax({
                    url: "../../preView/getNonLoginPreUrl.do",
                    data: {
                        productId,
                        roleId,
                        exampleName
                    },
                    success: function (result) {
                        if(isTrue(result.success)) {
                            var nonLoginPreUrl = result.data;
                            var iframe = '<iframe class="embed-responsive-item" id="iframe_showPreConsole" src="' + nonLoginPreUrl + '"></iframe>';
                            $("#preViewConsoleDiv").html(iframe);
                        }
                    }
                })
            }
        })
    })
})

function isTrue(isSuccess) {
    return (isSuccess === "true" || isSuccess === true);
}