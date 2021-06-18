$(document).ready(function () {
    $("#btn_config").on("click", function () {
        let accessKeyId = $("input[name='accessKeyId']").val();
        let accessKeySecret = $("input[name='accessKeySecret']").val();
//        let ecsJson = $('textarea[name="ecsJson"]').val();

        $("#msgBox").show();
        $("#msgBox").delay(1000).hide(500);
        $.ajax({
            url: "../../slsAutoConfig/getEcsList.do",
            type: "GET",
            data: {
                accessKey: accessKeyId,
                secretKey: accessKeySecret
            },
            success: function (result) {
                let accountList = result.data;
                let acccountDiv = '<ul class="list-group">';
                for (let index = 0; index < accountList.length; index++) {
                    let account = accountList[index];
                    acccountDiv += '<li class="list-group-item">' + JSON.stringify(account) + '</li>'
                }
                acccountDiv += '</ul>';
                $("#accountListDiv").html(acccountDiv);
            }
        });

        $.ajax({
            url: "../../slsAutoConfig/slsAutoConfig.do",
            type: "POST",
            data: {
                accessKey: accessKeyId,
                secretKey: accessKeySecret
//                ecsJson: ecsJson
            },
            success: function (result) {
                $("#consoleDiv").html(result);
            }
        });
    });

    $("#btn_listEcs").on("click", function () {
        let accessKeyId = $("input[name='accessKeyId']").val();
        let accessKeySecret = $("input[name='accessKeySecret']").val();
        $.ajax({
            url: "../../slsAutoConfig/getEcsList.do",
            data: {
                accessKey: accessKeyId,
                secretKey: accessKeySecret
            },
            success: function (result) {
                let accountList = result.data;
                let acccountDiv = '';
                for (let index = 0; index < accountList.length; index++) {
                    let account = accountList[index];
                    acccountDiv += '<div class="list-group-item"><h4 class="list-group-item-heading">' + account.displayName + '  &nbsp;[' + account.accountId + ']' + '</h4><p class="list-group-item-text">' + JSON.stringify(account) + '</p></div>';
                }
                $("#accountListDiv").html(acccountDiv);
            }
        });
    });

    $("#btn_rollback").on("click", function () {
        let accessKeyId = $("input[name='accessKeyId']").val();
        let accessKeySecret = $("input[name='accessKeySecret']").val();
        let ecsJson = $('textarea[name="ecsJson"]').val();
        $("#msgBox").show();
        $("#msgBox").delay(1000).hide(500);

        $.ajax({
            url: "../../slsAutoConfig/slsConfigRollback.do",
            type: "POST",
            data: {
                accessKey: accessKeyId,
                secretKey: accessKeySecret,
                ecsJson: ecsJson
            },
                success: function (result) {
                $("#consoleDiv").html(result);
            }
        });
    });
});