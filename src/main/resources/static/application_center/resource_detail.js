var resourceDetail;
var serviceName;
var resourceId;

$(document).ready(function() {
    var queryString = window.location.search;
    var urlParams = new URLSearchParams(queryString);
    serviceName = urlParams.get('serviceName');
    resourceId = urlParams.get('resourceId');
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
});

function showResourceDetail(resourceId) {
    var page = $("#appCenterPage");
    page.empty();
    var content = `
    <div class="row pb-2 d-flex">
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
        </tr>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>`;
    page.append(content);

    $("#"+resourceId+"-events-table tbody").empty();
    resourceDetail.events.forEach(function(event) {
        var row = `
        <tr>
            <td>${event.eventTime}</td>
            <td>
              <div>Name: ${event.userIdentity.userName}</div>
              <div>Type: ${event.userIdentity.type}</div>
            </td>
            <td>${event.eventName}</td>
        </tr>`;
        $("#"+resourceId+"-events-table tbody").append(row);
    });
}

function showAppCenterPage() {
    var page = "application_center/application_center.html";
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}