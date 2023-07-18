var appsInfo;
var appDetail;
var resourceDetail;
var sortedServiceNames = ["ECS", "RDS", "SLB", "OSS", "SLS"];

$(document).ready(function() {
    $("#appCenterPage").append(
        `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>`
    );
    $.ajax({
        url: "../apps/listApps.do",
        success: function(result){
            if (result.success) {
                appsInfo = result.data;
                console.log(appsInfo);
                showAppCenterPage();
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

// 应用中心
function showAppCenterPage() {
    $("#navbarResourceId").text("");
    var page = $("#appCenterPage");
    page.empty();
    var skeleton = `
    <div class="row d-flex">
        <div class="col input-group">
        <input id="searchApp" class="form-control" type="text" placeholder="Please enter the application name">
        <button class="btn btn-outline-secondary" type="button" id="button-search-app" onclick=createFilteredCards()><i class="bi bi-search"></i></button>
        </div>
        <div class="col">
        <button class="btn btn-outline-secondary" type="button" id="button-reset-search-app" onclick=createCards()><i class="bi bi-arrow-clockwise"></i></button>
        </div>
    </div>
    <div class="row g-4 py-3 row-cols-1 row-cols-lg-3" id="cardContainer">
    </div>`;
    page.append(skeleton);
    var akModal = `
    <div class="modal fade modal-lg" id="modalSuccess" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
        aria-hidden="true">
        <div class="modal-dialog modal-dialog-scrollable" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">查看 Secret</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p class="fw-bold text-primary">secretName:  <span id="secretName">init</span></p>
                    <div id="content"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="copyToken()">Copy token</button>
                </div>
            </div>
        </div>
    </div>`;
    page.append(akModal);
    createCards();
}

function createCards(appName) {
    $("#cardContainer").empty();
    appsInfo.forEach(function (app) {
        if (appName === undefined || app.appName.toLowerCase().includes(appName.toLowerCase())) {
            var card = `
            <div class="col align-items-start" id="card-${app.appName}">
                <div class="card">
                    <div class="card-body">
                        <div class="row">
                        <h4 class="col-9 card-title">
                            <a href="#" class="text-black text-decoration-none" onclick="getAppDetail('${app.appName}', 'all')">${app.appName}</a>
                        </h4>
                        <div class="col-3 d-flex justify-content-end">
                        <a class="iconfont icon-user text-black text-decoration-none" 
                        data-bs-toggle="tooltip" data-bs-title="AK Apply"
                        href="#" onclick=clickAK('${app.appName}')></a>
                        <span style="margin-left: 10px;"></span>
                        <a class="iconfont icon-dashboard text-black text-decoration-none" 
                        data-bs-toggle="tooltip" data-bs-title="Monitor Center"
                        href="#" onclick=jumpToMonitor('${app.appName}')></a>
                        <span style="margin-left: 10px;"></span>
                        <a class="iconfont icon-sls text-black text-decoration-none" 
                        data-bs-toggle="tooltip" data-bs-title="Log Center"
                        href="#" onclick=jumpToLogCenter('${app.appName}')></a>
                        </div>
                        </div>
                        <p class="card-text">${app.description}</p>
                        <div class="row row-cols-1 row-cols-lg-5" id="services-${app.appName}"></div>
                    </div>
                </div>
            </div>`;
            $("#cardContainer").append(card);
            for (var serviceName of sortedServiceNames) {
                var service = `
                <a href="#" class="col align-items-start text-black text-decoration-none" id=${app.appName}-${serviceName} onclick="getAppDetail('${app.appName}', '${serviceName}')">
                    <h5>${app.servicesCounts[serviceName]}</h5>
                    <p>${serviceName}</p>
                </a>`;
                $("#services-" + app.appName).append(service);
            };
        }
    });
    $(function() { $("[data-bs-toggle='tooltip']").tooltip();});
    if (appName === undefined) {
        $("#searchApp").val("");
    }
}

function createFilteredCards() {
    var appName = $("#searchApp").val();
    createCards(appName);
}

// 应用详情
function getAppDetail(appName, serviceName) {
    var page = "application_center/application_detail.html?appName=" + appName + "&serviceName=" + serviceName;
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}

// 跳转到日志中心
function jumpToLogCenter(appName) {
    var message = {};
    message.destination = "log_center";
    message.url = "log_center/log_center.html?appName=" + appName;
    window.parent.postMessage(message, "*");
}

// 跳转到monitor
function jumpToMonitor(appName) {
    var message = {};
    message.destination = "monitor";
    message.url = "monitor/monitor.html?appName=" + appName;
    window.parent.postMessage(message, "*");
}

// 跳转到AKApply
function jumpToAKApply(appName) {
    var message = {};
    message.destination = "ak_apply";
    message.url = "ak_apply/ak_apply.html?appName=" + appName;
    window.parent.postMessage(message, "*");
}

// 点击AK申请icon
function clickAK(appName) {
    var params = {
        applicationName: appName,
    };

    $.ajax({
        url: "../" + "akApply/checkSecretName.do",
        type: "POST",
        data: params,
        success: function (result) {
            if (result.success) {
                if (result.data == null || result.data == "") {
                    // 跳转
                    jumpToAKApply(appName);
                } else {
                    secretName = result.data;
                    document.getElementById("secretName").innerText = secretName;
                    $.ajax({
                        url: "../" + "akApply/getSecretNameUseSample.do",
                        type: "POST",
                        data: {},
                        success: function (result) {
                            if (result.success) {
                                var secretNameUseSample = result.data;
                                document.getElementById('content').innerHTML = marked.parse(secretNameUseSample);
                                $('#modalSuccess').modal('show');
                            } else {
                                console.log("data.message: " + result.errorMsg);
                                alert(result.errorMsg);
                            }
                        },
                    });
                }

            } else {
                console.log("data.message: " + result.errorMsg);
                alert(result.errorMsg);
            }
        },
    });
}

async function copyToken() {
    try {
        var secretName = document.getElementById("secretName").innerText;
        var copyText = secretName;
        await navigator.clipboard.writeText(copyText);
        alert('Content copied to clipboard');
    } catch (err) {
        alert('Failed to copy: ', err);
    }
}