var templateContentEditor;
var createFailEditor;
var instanceType;
$(document).ready(function () {
    // 单选按钮点击事件
    $('input[type=radio][name=resourceType]').change(function () {
        var resourceType = $("input[name='resourceType']:checked").val();
        if (resourceType == "ecs") {
            $("#ecsTemplate").show();
            $("#ossTemplate").hide();
            $("#logTemplate").hide();
            $("#ecsResoureProperties").show();
            $("#ossResoureProperties").hide();
            $("#logResoureProperties").hide();
        } else if (resourceType == "oss") {
            $("#ecsTemplate").hide();
            $("#ossTemplate").show();
            $("#logTemplate").hide();
            $("#ecsResoureProperties").hide();
            $("#ossResoureProperties").show();
            $("#logResoureProperties").hide();
        } else if (resourceType == "log") {
            $("#ecsTemplate").hide();
            $("#ossTemplate").hide();
            $("#logTemplate").show();
            $("#ecsResoureProperties").hide();
            $("#ossResoureProperties").hide();
            $("#logResoureProperties").show();
        }
    });

    $('input[type=radio][name=environmentType]').change(function () {
        getVpcList();
    });

    getApplication();

    templateContentEditor = CodeMirror.fromTextArea(document.getElementById("templateContent"), {
        mode: "text/x-java", // 实现Java代码高亮
        lineNumbers: true,  //显示行号
        theme: "default",   //设置主题
        lineWrapping: false, //false则超过宽带会显示水平滚动条，true不会显示
        foldGutter: true,   //代码是否可折叠
        gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
        matchBrackets: true,    //括号匹配
        indentWithTabs: true,  //前 N*tabSize 个空格是否应替换为 N 个制表符
        smartIndent: true,   //上下文相关缩进（即是否缩进与之前的行相同）
        autofocus: true,
        styleActiveLine: true, //光标所在行高亮
        //readOnly: true,      //只读
    });
    templateContentEditor.setSize(null, "510px");

    createFailEditor = CodeMirror.fromTextArea(document.getElementById("createFailMessageContent"), {
        mode: "null",
        theme: "night",   //设置主题
        lineWrapping: true, //false则超过宽带会显示水平滚动条，true不会显示
        foldGutter: true,   //代码是否可折叠
        matchBrackets: true,    //括号匹配
        indentWithTabs: true,  //前 N*tabSize 个空格是否应替换为 N 个制表符
        smartIndent: true,   //上下文相关缩进（即是否缩进与之前的行相同）
        autofocus: true,
        styleActiveLine: true, //光标所在行高亮
        //readOnly: true,      //只读
    });

  $(".card").click(function() {
    $(".card").removeClass("selected"); // 移除所有卡片的选中状态
    $(this).addClass("selected"); // 添加当前点击的卡片的选中状态

    instanceType = $(this).find(".card-title").text();
  });

});

var links = document.querySelectorAll(".navbar-nav a");
var iframe = document.getElementById("iframe");

links.forEach(function (link) {
    link.addEventListener("click", function (e) {
        e.preventDefault();
        var page = e.target.getAttribute("data-link");
        iframe.setAttribute("src", page);

        // Remove active class from all links
        deleteAccessKeyRequest = new com.aliyun.ram20150501.models.DeleteAccessKeyRequest()
            .setUserName("aaa")
            .setUserAccessKeyId("111"); links.forEach(function (link) {
                link.classList.remove("active");
            });

        // Add active class to the clicked link
        e.target.classList.add("active");
    });
});

function getVpcList() {
    var application = $("#application").val();
    var environmentType = $("input[name='environmentType']:checked").val();
    var params = {
        applicationName: application,
        environmentName: environmentType,
    };
    $.ajax({
        url: "../" + "resourceSupply/getVpcList.do",
        type: "POST",
        data: params,
        contentType: "application/x-www-form-urlencoded",
        success: function (result) {
            if (result.success) {
                var vpc = document.getElementById("vpc");
                for (var i = vpc.length - 1; i >= 1; i--) {
                    vpc.remove(i);
                }
                data = result.data;
                for (var i = 0; i < data.length; i++) {
                    var vpcId = data[i].split("/")[1].trim();
                    console.log("vpcId: " + vpcId);
                    vpc.options.add(new Option(data[i], vpcId));
                }
                vpc.value = "";
            } else {
                console.log("result.errorMsg: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    })
}



function getVSwitches() {
    var application = $("#application").val();
    var environmentType = $("input[name='environmentType']:checked").val();
    var vpcId = $("#vpc").val();

    var params = {
        applicationName: application,
        environmentName: environmentType,
        vpcId: vpcId
    };

    $.ajax({
        url: "../" + "resourceSupply/getVSwitches.do",
        type: "POST",
        data: params,
        contentType: "application/x-www-form-urlencoded",
        success: function (result) {
            if (result.success) {
                var vSwitch = document.getElementById("vSwitch");
                for (var i = vSwitch.length - 1; i >= 1; i--) {
                    vSwitch.remove(i);
                }
                data = result.data;
                for (var i = 0; i < data.length; i++) {
                    var vSwitchId = data[i].split("/")[1].trim();
                    console.log("vSwitchId: " + vSwitchId);
                    vSwitch.options.add(new Option(data[i], vSwitchId));
                }
                vSwitch.value = "";
            } else {
                console.log("result.errorMsg: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    })
}

function getTemplateByResourceType(fileType) {
    var tabs = document.querySelectorAll(".nav-tabs a");
    tabs.forEach(function (tab) {
        tab.classList.remove("active");
    });
    document.querySelector("#tab-" + fileType).classList.add("active");

    var resourceType = $("input[name='resourceType']:checked").val();
    console.log("resourceType: " + resourceType);
    if (fileType == "terraform") {
        templateContentEditor.setOption("mode", "text/x-go");
    } else {
        templateContentEditor.setOption("mode", "text/x-java");
    }

    var params = {
        resourceType: resourceType,
        fileType: fileType
    };

    $.ajax({
        url: "../" + "resourceSupply/getTemplateByResourceType.do",
        type: "POST",
        data: params,
        success: function (result) {
            if (result.success) {
                templateContentEditor.setValue(result.data);
            } else {
                console.log("result.errorMsg: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    })
}

function createResource() {
    var resourceType = $("input[name='resourceType']:checked").val();
    $("#createFail").hide();
    $("#createFailMessage").hide();
    $("#createSuccess").hide();
    $("#creating").show();
    if (resourceType == "ecs") {
        createEcsInstance();
    } else if (resourceType == "oss") {
        createOssBucket();
    } else if (resourceType == "log") {
        createLogProject();
    }
    return false;
}

function createEcsInstance() {
    var regionId = $("#regionId").val();
    var vpcId = $("#vpc").val();
    var vSwitchId = $("#vSwitch").val();
    // var instanceType = $("#instanceType").val();
    var amount = $("#instanceNum").val();
    var application = $("#application").val();
    var environmentType = $("input[name='environmentType']:checked").val();
//    var instanceName = $("#instanceName").val();
    if (instanceType == "") {
        alert("请选择实例类型");
        return;
    }

    var params = {
        applicationName: application,
        environmentName: environmentType,
        regionId: regionId,
        vpcId: vpcId,
        vSwitchId: vSwitchId,
        instanceType: instanceType,
        amount: amount,
//        instanceName: instanceName
    };

    $.ajax({
        url: "../" + "resourceSupply/createEcsInstance.do",
        type: "POST",
        data: params,
        success: function (result) {
            $("#creating").hide();
            if (result.success) {
                $("#createSuccess").show();
            } else {
                $("#createFail").show();
                $("#createFailMessage").show();
                createFailEditor.setValue(result.errorMsg);
            }
        },
    })
}

function createOssBucket() {
    var bucketName = $("#bucketName").val();
    var application = $("#application").val();
    var environmentType = $("input[name='environmentType']:checked").val();

    var params = {
        bucketName: bucketName,
        applicationName: application,
        environmentName: environmentType
    };

    $.ajax({
        url: "../" + "resourceSupply/createOssBucket.do",
        type: "POST",
        data: params,
        success: function (result) {
            $("#creating").hide();
            if (result.success) {
                $("#createSuccess").show();
            } else {
                $("#createFail").show();
                $("#createFailMessage").show();
                createFailEditor.setValue(result.errorMsg);
            }
        },
    })
}

function createLogProject() {
    var projectName = $("#projectName").val();
    var description = $("#projectDescription").val();
    var application = $("#application").val();
    var environmentType = $("input[name='environmentType']:checked").val();

    var params = {
        projectName: projectName,
        description: description,
        applicationName: application,
        environmentName: environmentType
    };

    $.ajax({
        url: "../" + "resourceSupply/createLogProject.do",
        type: "POST",
        data: params,
        success: function (result) {
            $("#creating").hide();
            if (result.success) {
                $("#createSuccess").show();
            } else {
                $("#createFail").show();
                $("#createFailMessage").show();
                createFailEditor.setValue(result.errorMsg);
            }
        },
    })
}

function getApplication() {
    $.ajax({
        url: "../" + "common/getApplication.do",
        type: "POST",
        data: {},
        success: function (result) {
            if (result.success) {
                var application = document.getElementById("application");
                var data = result.data;
                for (var i = application.length - 1; i >= 1; i--) {
                    application.remove(i);
                }
                for (var i = 0; i < data.length; i++) {
                    application.options.add(new Option(data[i], data[i]));
                }
                application.value = "";
            } else {
                console.log("data.message: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    })
}