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
                                <li><a class="dropdown-item" href="#" onclick=jumpToMonitor('${app.appName}')>Monitor</a></li>
                                <li><a class="dropdown-item" href="#" onclick=jumpToLogCenter('${app.appName}')>Log Center</a></li>
                            </ul>
                        </div>
                        <h4 class="card-title">
                            <a href="#" class="text-black text-decoration-none" onclick="getAppDetail('${app.appName}', 'all')">${app.appName}</a>
                        </h4>
                        <p class="card-text">${app.description}</p>
                        <div class="row row-cols-1 row-cols-lg-5" id="services-${app.appName}"></div>
                    </div>
                </div>
            </div>`;
            $("#cardContainer").append(card);
            for (var serviceName in app.servicesCounts) {
                var service = `
                <a href="#" class="col align-items-start text-black text-decoration-none" id=${app.appName}-${serviceName} onclick="getAppDetail('${app.appName}', '${serviceName}')">
                    <h5>${app.servicesCounts[serviceName]}</h5>
                    <p>${serviceName}</p>
                </a>`;
                $("#services-" + app.appName).append(service);
            };
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