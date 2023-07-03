var appName;
var serviceName;
var appInfo;
var appDetail;

$(document).ready(function() {
    var queryString = window.location.search;
    var urlParams = new URLSearchParams(queryString);
    appName = urlParams.get('appName');
    serviceName = urlParams.get('serviceName');
    var appDetailPage = `
    <div class="row pb-2 d-flex">
        <h5 class="pb-3 fs-2">Resources - ${appName}</h5>
        <div class="row pb-2" id="appDetailBody">
        </div>
    </div>`;
    $("#appCenterPage").append(appDetailPage);
    $.ajax({
        url: "../apps/listApps.do",
        success: function(result){
            if (result.success) {
                appsInfo = result.data;
                $("#appDetailBody").empty();
                var tab = `<ul class="nav nav-tabs" id="tabs"></ul>`;
                $("#appDetailBody").append(tab);
                appsInfo.forEach(function (app) {
                    if (app.appName === appName) {
                        var sumCount = 0;
                        for (var serviceName in app.servicesCounts) {
                            sumCount += app.servicesCounts[serviceName];
                        };
                        $("#tabs").append(`<li class="nav-item">
                            <a class="nav-link text-black" aria-current="page" id="tab-all" onclick="listResourcesDetails('all')">All(${sumCount})</a>
                            </li>`);
                        for (var serviceName in app.servicesCounts) {
                            var tab = `
                            <li class="nav-item">
                                <a class="nav-link text-black" aria-current="page" id="tab-${serviceName}"
                                    onclick="listResourcesDetails('${serviceName}')">${serviceName}(${app.servicesCounts[serviceName]})</a>
                            </li>`;
                            $("#tabs").append(tab);
                        };
                    }
                });
                var table = `
                <table class="table">
                    <thead>
                    <tr>
                        <th scope="col">Resource ID/name</th>
                        <th scope="col">Resource Type</th>
                        <th scope="col">Environment Type</th>
                        <th scope="col">Region</th>
                        <th scope="col">Operations</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            `;
            $("#appDetailBody").append(table);
            $("#appDetailBody tbody").append(
                `<div class="position-absolute top-50 start-50 translate-middle">
                <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
                <span class="visually-hidden">Loading...</span>
                </div>
            </div>`
            );
            } else {
                alert(result.errorMsg);
            }
        }
    });
    $.ajax({
        url: "../apps/getAppDetail.do?appName="+appName,
        success: function(result){
            if (result.success) {
                appDetail = result.data;
                listResourcesDetails(serviceName);
                console.log(appDetail);
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function listResourcesDetails(queryServiceName) {
    var tabs = document.querySelectorAll("#tabs a");
    tabs.forEach(function(tab) {
        tab.classList.remove("active");
    });
    document.querySelector("#tab-"+queryServiceName).classList.add("active");
    $("#appDetailBody tbody").empty();
    
    for (var serviceName in appDetail) {
        if (queryServiceName === "all" || serviceName === queryServiceName) {
            appDetail[serviceName].forEach(function(resource) {
                var row = `
                <tr>
                    <td><a href=# class="text-decoration-none" onclick="getResourceDetail('${serviceName}', '${resource.resourceId}')">${resource.resourceId}</a></td>
                    <td>${resource.serviceName}</td>
                    <td>${resource.environmentType}</td>
                    <td>${resource.regionId}</td>
                    <td>
                        <a href=# class="text-decoration-none" onclick="getResourceDetail('${serviceName}', '${resource.resourceId}')">查看</a>
                        <a target="_blank" class="text-decoration-none" href="../apps/signInConsole.do?serviceName=${serviceName}&regionId=${resource.regionId}&resourceId=${resource.resourceId}">Console</a>
                        <a target="_blank" class="text-decoration-none" href="${resource.operations["operationUrl"]}">${resource.operations["operationName"]}</a>
                    </td>
                </tr>`;

                $("#appDetailBody tbody").append(row);
            })
        }
    }
}

// 资源详情
function getResourceDetail(serviceName, resourceId) {
    var page = "application_center/resource_detail.html?serviceName=" + serviceName + "&resourceId=" + resourceId;
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}

// 返回应用中心
function showAppCenterPage() {
    var page = "application_center/application_center.html";
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}