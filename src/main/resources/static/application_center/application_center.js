var appsInfo;
var resourcesDetails;
$(document).ready(function() {
    $.ajax({
        url: "../apps/listApps.do",
        success: function(result){
            if (result.success) {
                appsInfo = result.data;
                console.log(appsInfo);
                createCards();
            } else {
                alert(result.errorMsg);
            }
        }
    });

    $.ajax({
        url: "../apps/getAppDetail.do",
        success: function(result){
            if (result.success) {
                resourcesDetails = result.data;
                console.log(resourcesDetails);
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
                            <a class="text-black text-decoration-none" data-bs-toggle="offcanvas" href="#offcanvas-${app.appName}" role="button" aria-controls="offcanvasExample">${app.appName}</a>
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
                <a class="nav-link active" aria-current="page" id="tab-${app.appName}-all" onclick="listResourcesDetails('${app.appName}', 'all')">All</a>
                </li>`);
            for (var serviceName in app.servicesCounts) {
                var tab = `
                <li class="nav-item">
                    <a class="nav-link" aria-current="page" id="tab-${app.appName}-${serviceName}"
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
                        <th scope="col">Create Time</th>
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


function listResourcesDetails(appName, queryServiceName) {
    var tabs = document.querySelectorAll("#tabs-"+appName+" a");
    tabs.forEach(function(tab) {
        tab.classList.remove("active");
    });
    document.querySelector("#tab-"+appName+"-"+queryServiceName).classList.add("active");
    $("#offcanvas-body-"+appName+" tbody").empty();
    
    for (var serviceName in resourcesDetails) {
        if (queryServiceName === "all" || serviceName === queryServiceName) {
            resourcesDetails[serviceName].forEach(function(resource) {
                var row = `
                <tr>
                    <td>
                        <p>${resource.resourceId}</p>
                        <p>${resource.resourceName}</p>
                    </td>
                    <td>${resource.serviceName}</td>
                    <td>${resource.environmentType}</td>
                    <td>${resource.regionId}</td>
                    <td>${resource.createTime}</td>
                    <td>${resource.operations}</td>
                </tr>`;

                $("#offcanvas-body-"+appName+" tbody").append(row);
            })
        }
    }
}