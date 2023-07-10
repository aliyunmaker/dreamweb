var resourcesCounts;
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
        url: "../resources/listResourcesByRegion.do",
        success: function(result){
            if (result.success) {
                resourcesCounts = result.data;
                console.log(resourcesCounts);
                showResourceCenterPage();
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function showResourceCenterPage() {
    var page = $("#resourceCenterPage");
    page.empty();
    var skeleton = `
    <div class="row g-4 py-3 row-cols-1 row-cols-lg-3" id="cardContainer">
    </div>`;
    page.append(skeleton);
    createCards();
}

function createCards() {
    $("#cardContainer").empty();
    for (var region in resourcesCounts) {
        var card = `
        <div class="col align-items-start" id="card-${region}">
            <div class="card">
                <div class="card-body">
                    <h4 class="col-9 card-title">${region}</h4>
                    <p class="card-text"></p>
                    <div class="row row-cols-1 row-cols-lg-5" id="services-${region}"></div>
                </div>
            </div>
        </div>`;
        $("#cardContainer").append(card);
        for (var serviceName of sortedServiceNames) {
            var service = `
            <div class="col align-items-start">
            <h5>${resourcesCounts[region][serviceName]}</h5>
            <p>${serviceName}</p>
            </div>`;
            $("#services-" + region).append(service);
        };
    };
}