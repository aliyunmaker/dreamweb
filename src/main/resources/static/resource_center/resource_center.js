var accountRegionResourcesCounts;
var resourceDirectoryId;
var adminName;
var accountsWithoutResources;
var sortedServiceNames = ["ECS", "RDS", "SLB", "OSS", "SLS"];

$(document).ready(function() {
    $("#resourceCenterPage").append(
    `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );
    $.ajax({
        url: "../resources/listResources.do",
        success: function(result){
            if (result.success) {
                accountRegionResourcesCounts = result.data.accountRegionResourcesCounts;
                resourceDirectoryId = result.data.resourceDirectoryId;
                adminName = result.data.resourceCenterAdminName;
                accountsWithoutResources = result.data.accountsWithoutResources;
                console.log(result.data);
                showResourceCenterPage(resourceDirectoryId);
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function showResourceCenterPage(accountName) {
    var page = $("#resourceCenterPage");
    page.empty();
    var skeleton = `
    <div class="container">
    <div class="row">
        <div class="col-3 d-flex flex-column align-items-stretch flex-shrink-0 bg-white">
            <div class="d-flex align-items-center flex-shrink-0 p-3 link-dark text-decoration-none border-bottom">
                <i class="iconfont icon-user"></i>
            <span class="fs-5 fw-semibold">&nbsp;&nbsp;Accounts</span>
            </div>
            <div class="list-group list-group-flush border-bottom scrollarea" id="accountContainer">
            <a href="#" class="list-group-item list-group-item-action active py-3 lh-tight" aria-current="true" id="account-${resourceDirectoryId}" onclick="createCards('${resourceDirectoryId}')">
                <div class="d-flex w-100 align-items-center justify-content-between">
                <strong class="mb-1">${resourceDirectoryId}</strong>
                </div>
            </a>
            <a href="#" class="list-group-item list-group-item-action active py-3 lh-tight" aria-current="true" id="account-${adminName}" onclick="createCards('${adminName}')">
                <div class="d-flex w-100 align-items-center justify-content-between">
                <strong class="mb-1">&nbsp;&nbsp;&nbsp;&nbsp;${adminName}</strong>
                </div>
            </a>
            </div>
        </div>
        <div class="col-9 row g-4 py-3 row-cols-1 row-cols-lg-2" id="cardContainer">
        </div>
    </div>
    </div>`;
    page.append(skeleton);
    listAccounts();
    createCards(accountName);
}

function listAccounts() {
    for (var accountName in accountRegionResourcesCounts) {
        if (accountName === resourceDirectoryId || accountName === adminName) {
            continue;
        }
        var accoutItem = `
        <a href="#" class="list-group-item list-group-item-action py-3 lh-tight" aria-current="true" id="account-${accountName}" onclick="createCards('${accountName}')">
            <div class="d-flex w-100 align-items-center justify-content-between">
            <strong class="mb-1">&nbsp;&nbsp;&nbsp;&nbsp;${accountName}</strong>
            </div>
        </a>`;
        $("#accountContainer").append(accoutItem);
    }
    for (var accountName of accountsWithoutResources) {
        if (accountName === resourceDirectoryId || accountName === adminName) {
            continue;
        }
        var accoutItem = `
        <a href="#" class="list-group-item disabled list-group-item-action py-3 lh-tight" aria-current="true" id="account-${accountName}" onclick="createCards('${accountName}')">
            <div class="d-flex w-100 align-items-center justify-content-between">
            <strong class="mb-1">&nbsp;&nbsp;&nbsp;&nbsp;${accountName}</strong>
            </div>
        </a>`;
        $("#accountContainer").append(accoutItem);
    }
}

function createCards(accountName) {
    var links = document.querySelectorAll("#accountContainer a");
    links.forEach(function (link) {
        link.classList.remove("active");
    });

    // Add active class to the clicked link
    document.getElementById("account-" + accountName).classList.add("active");

    $("#cardContainer").empty();
    var regionResourcesCounts = accountRegionResourcesCounts[accountName];
    for (var regionId in regionResourcesCounts) {
        var card = `
        <div class="col align-items-start" id="card-${accountName}-${regionId}">
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title">${regionId}</h4>
                    <p class="card-text"></p>
                    <div class="row row-cols-1 row-cols-lg-5" id="services-${accountName}-${regionId}"></div>
                </div>
            </div>
        </div>`;
        $("#cardContainer").append(card);
        for (var serviceName of sortedServiceNames) {
            var service = `
            <div class="col align-items-start">
            <h5>${regionResourcesCounts[regionId][serviceName]}</h5>
            <p>${serviceName}</p>
            </div>`;
            $("#services-" + accountName + "-" + regionId).append(service);
        };
    };
}

function getDocumentByModule(){
    var params = {
      module: "resourcecenter"
    }
    $.ajax({
      url: "../" + "common/getDocumentByModule.do",
      type: "POST",
      data: params,
      success: function (result) {
        if (result.success) {
          var documentContent = result.data;
           document.getElementById('documentContent').innerHTML = marked.parse(documentContent);
        } else {
          console.log("data.message: " + result.errorMsg);
          alert(result.errorMsg);
        }
      }
    })
  }