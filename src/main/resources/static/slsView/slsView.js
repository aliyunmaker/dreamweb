$(document).ready(function () {
    $.ajax({
        url: "../../slsView/getRoles.do",
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

    $("#select_role").change(function () {
        $("#select_project").empty();
        $("#select_logstore").empty();

        var selectedRole = JSON.parse($(this).val());
        var roleId = selectedRole.id;

        $.ajax({
            url: "../../slsView/getProjects.do",
            data: {
                start: 0,
                limit: 500,
                roleId: roleId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var projectList = result.data;
                    if(projectList.length == 0) {
                    alert("无法在该账号下查询到Project，请检查账号配置！");
                        return;
                    }

                    for (var i = 0; i < projectList.length; i++) {
                        var projectName = projectList[i];

                        var option = "<option value='" + projectName + "'>" + projectName + "</option>";
                        $("#select_project").append(option);
                    }

                    $.ajax({
                        url: "../../slsView/getLogstores.do",
                        data: {
                            projectName: projectList[0],
                            start: 0,
                            limit: 500,
                            roleId: roleId
                        },
                        success: function (result) {
                            if(isTrue(result.success)) {
                                var logstoreList = result.data;
                                if(logstoreList.length == 0) {
                                    alert("目标Project下Logstore为空！");
                                    return;
                                }

                                for(var i = 0; i < logstoreList.length; i++) {
                                    var logstoreName = logstoreList[i];
                                    var option = '<option value="' + logstoreName + '">' + logstoreName + '</option>';
                                    $("#select_logstore").append(option);
                                }
                            } else {
                                alert(result.errorMsg);
                            }
                        }
                    });
                } else {
                   alert(result.errorMsg);
                }
            }
        });
    });

    $("#select_project").change(function () {
        $("#select_logstore").empty();

        var selectedRole = JSON.parse($("#select_role").val());
        var roleId = selectedRole.id;
        var projectName = $(this).val();

        $.ajax({
            url: "../../slsView/getLogstores.do",
            data: {
                projectName: projectName,
                start: 0,
                limit: 500,
                roleId: roleId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var logstoreList = result.data;
                    if(logstoreList.length == 0) {
                        alert("目标Project下Logstore为空！");
                        return;
                    }

                    for(var i = 0; i < logstoreList.length; i++) {
                        var logstoreName = logstoreList[i];
                        var option = '<option value="' + logstoreName + '">' + logstoreName + '</option>';
                        $("#select_logstore").append(option);
                    }
                } else {
                    alert(result.errorMsg);
                }
            }
        });
    });

    $("#btn_getSlsConsoleUrl").on("click", function () {
        var projectName = $("#select_project").val();
        var logstoreName = $("#select_logstore").val();
        var selectedRole = JSON.parse($("#select_role").val());
        var roleId = selectedRole.id;

        $.ajax({
            url: "../../slsView/getNonLoginSlsUrl.do",
            data: {
                projectName: projectName,
                logstoreName: logstoreName,
                roleId: roleId
            },
            success: function (result) {
                if(isTrue(result.success)) {
                    var nonLoginSlsUrl = result.data;
                    var iframe = '<iframe class="embed-responsive-item" id="iframe_showSlsConsole" src="' + nonLoginSlsUrl + '"></iframe>';
                    $("#slsConsoleDiv").html(iframe);
                } else {
                    alert(result.errorMsg);
                }
            }
        });
    });
});

function isTrue(isSuccess) {
    return (isSuccess === "true" || isSuccess === true);
}