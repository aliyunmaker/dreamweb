$(document).ready(function () {
    $.ajax({
        url: "../../slsView/getProjects.do",
        data: {
            start: 0,
            limit: 1000
        },
        success: function (result) {
           if(isTrue(result.success)) {
               var projectList = result.data;
               if(projectList.length == 0) {
               alert("无法在该账号下查询到Project，请检查账号配置！");
                   return;
               }

               for (var i = 0; i < projectList.length; i++) {
                   var projectName = projectList[i].projectName;
                   var region = projectList[i].region;

                   var optionValue = '{"projectName":"' + projectName + '","region":"' + region + '"}';
                   var option = "<option value='" + optionValue + "'>" + projectName + "</option>";
                   $("#select_project").append(option);
               }
           } else {
               alert(result.errorMsg);
           }
        }
    });

    $("#select_project").change(function () {
        $("#select_logstore").empty();

        var selectedProject = JSON.parse($(this).val());
        var projectName = selectedProject.projectName;
        var region = selectedProject.region;

        $.ajax({
            url: "../../slsView/getLogstores.do",
            data: {
                projectName: projectName,
                region: region,
                start: 0,
                limit: 1000
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
        var selectedProject = JSON.parse($("#select_project").val());
        var projectName = selectedProject.projectName;
        var region = selectedProject.region;

        var logstoreName = $("#select_logstore").val();

        $.ajax({
            url: "../../slsView/getNonLoginSlsUrl.do",
            data: {
                projectName: projectName,
                logstoreName: logstoreName,
                region: region
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