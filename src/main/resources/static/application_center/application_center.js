var appsInfo;
var appDetail;
var resourceDetail;
$(document).ready(function() {
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

var links = document.querySelectorAll(".navbar-nav a");
var iframe = document.getElementById("iframe");

links.forEach(function (link) {
    link.addEventListener("click", function (e) {
        e.preventDefault();
        var page = e.target.getAttribute("data-link");
        iframe.setAttribute("src", page);

        // Remove active class from all links
        links.forEach(function (link) {
            link.classList.remove("active");
        });

        // Add active class to the clicked link
        e.target.classList.add("active");
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
                        <div class="dropdown-left float-end">
                            <a href="#" class="d-flex align-items-center text-black text-decoration-none dropdown-toggle"
                            data-bs-toggle="dropdown" aria-expanded="false"></a>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="#">Dashboard</a></li>
                                <li><a class="dropdown-item" href="#">Log Center</a></li>
                            </ul>
                        </div>
                        <h4 class="card-title">
                            <a class="text-black text-decoration-none" data-bs-toggle="offcanvas"
                             href="#offcanvas-${app.appName}" role="button" onclick=getAppDetail('${app.appName}') aria-controls="offcanvasExample">${app.appName}</a>
                        </h4>
                        <p class="card-text">${app.description}</p>
                        <div class="row row-cols-1 row-cols-lg-5" id="services-${app.appName}"></div>
                    </div>
                </div>
            </div>`;
            $("#cardContainer").append(card);
            for (var serviceName in app.servicesCounts) {
                var service = `
                <div class="col align-items-start" id=${app.appName}-${serviceName}>
                    <h5>${app.servicesCounts[serviceName]}</h5>
                    <p>${serviceName}</p>
                </div>`;
                $("#services-" + app.appName).append(service);
            };
            var appDetail = `
            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvas-${app.appName}" aria-labelledby="offcanvasExampleLabel" style="--bs-offcanvas-width: 80%;">
                <div class="offcanvas-header">
                    <h5 class="offcanvas-title" id="offcanvasLabel-${app.appName}">${app.appName}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>
                <div class="offcanvas-body" id="offcanvas-body-${app.appName}">
                </div>
            </div>`;
            $("#card-"+app.appName).append(appDetail);
            var offcanvasTab = `<ul class="nav nav-tabs" id="tabs-${app.appName}"></ul>`;
            $("#offcanvas-body-"+app.appName).append(offcanvasTab);
            $("#tabs-"+app.appName).append(`<li class="nav-item">
                <a class="nav-link text-black active" aria-current="page" id="tab-${app.appName}-all" onclick="listResourcesDetails('${app.appName}', 'all')">All</a>
                </li>`);
            for (var serviceName in app.servicesCounts) {
                var tab = `
                <li class="nav-item">
                    <a class="nav-link text-black" aria-current="page" id="tab-${app.appName}-${serviceName}"
                     onclick="listResourcesDetails('${app.appName}', '${serviceName}')">${serviceName}(${app.servicesCounts[serviceName]})</a>
                </li>`;
                $("#tabs-" + app.appName).append(tab);
            };
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
            $("#offcanvas-body-"+app.appName).append(table);
        }
    });
    if (appName === undefined) {
        $("#searchApp").val("");
    }
}

function createFilteredCards() {
    var appName = $("#searchApp").val();
    createCards(appName);
}

// 应用详情
function getAppDetail(appName) {
    $.ajax({
        url: "../apps/getAppDetail.do?appName="+appName,
        success: function(result){
            if (result.success) {
                appDetail = result.data;
                listResourcesDetails(appName, 'all');
                console.log(appDetail);
            } else {
                alert(result.errorMsg);
            }
        }
    });
}

function listResourcesDetails(appName, queryServiceName) {
    var tabs = document.querySelectorAll("#tabs-"+appName+" a");
    tabs.forEach(function(tab) {
        tab.classList.remove("active");
    });
    document.querySelector("#tab-"+appName+"-"+queryServiceName).classList.add("active");
    $("#offcanvas-body-"+appName+" tbody").empty();
    
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
                        <a target="_blank" class="text-decoration-none" href=${resource.operations["consoleUrl"]}>Console</a>
                        <a href="${resource.operations["operationUrl"]}" class="text-decoration-none">${resource.operations["operationName"]}</a>
                    </td>
                </tr>`;

                $("#offcanvas-body-"+appName+" tbody").append(row);
            })
        }
    }
}

// 资源详情
function getResourceDetail(serviceName, resourceId) {
    $("#navbarResourceId").text("ResourceId: "+resourceId);
    $.ajax({
        url: "../apps/getResourceDetail.do?serviceName="+serviceName+"&resourceId="+resourceId,
        success: function(result){
            if (result.success) {
                resourceDetail = result.data;
                showResourceDetail(resourceId);
                console.log(resourceDetail);
            } else {
                alert(result.errorMsg);
            }
        }
    });
}

function showResourceDetail(resourceId) {
    var page = $("#appCenterPage");
    page.empty();
    var content = `
    <h3 class="pb-3 fs-2">Resource Detail</h3>
      <h4 class="pb-2 fs-4">Basic Information</h4>
      <div class="row pb-2">
        <p class="col-2 fw-semibold">
          Resource ID
        </p>
        <p class="col-4">
          ${resourceDetail.resource.resourceId}
        </p>
        <p class="col-2 fw-semibold">
          Resource Name
        </p>
        <p class="col-4">
          ${resourceDetail.resource.resourceName}
        </p>
      </div>
      <div class="row pb-2">
        <p class="col-2 fw-semibold">
          Resource Type
        </p>
        <p class="col-4">
          ${resourceDetail.resource.resourceType}
        </p>
        <p class="col-2 fw-semibold">
          Resource Region
        </p>
        <p class="col-4">
          ${resourceDetail.resource.regionId}
        </p>
      </div>
      <div class="row pb-4">
        <p class="col-2 fw-semibold">
          Create Time
        </p>
        <p class="col-4">
          ${resourceDetail.resource.createTime}
        </p>
      </div>
      <h4 class="pb-2 fs-4">Event History</h4>
        <table class="table" id=${resourceId}-events-table>
            <thead>
            <tr>
                <th scope="col">Event Time</th>
                <th scope="col">Operator</th>
                <th scope="col">Event Name</th>
                <th scope="col">Resource</th>
                <th scope="col">Operations</th>
            </tr>
            </thead>
            <tbody>
            </tbody>
        </table>`;
    page.append(content);

    $("#"+resourceId+"-events-table tbody").empty();
    resourceDetail.events.forEach(function(event) {
        var row = `
        <tr>
            <td>${event.eventTime}</td>
            <td>
              <div>name: ${event.userIdentity.userName}</div>
              <div>type: ${event.userIdentity.type}</div>
            </td>
            <td>${event.eventName}</td>
            <td>${event.resource}</td>
            <td>Operations</td>
        </tr>`;
        $("#"+resourceId+"-events-table tbody").append(row);
    })
}