var ruleId;
var ruleDetail;
var riskLevelWords = ["High", "Mid", "Low"];

$(document).ready(function() {
    var queryString = window.location.search;
    var urlParams = new URLSearchParams(queryString);
    ruleId = urlParams.get('ruleId');

    $("#ruleDetailPage").append(
    `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );

    var params = {
        ruleId: ruleId
      }
    
    $.ajax({
        url: "../inspection/getRuleDetail.do?",
        data: params,
        success: function(result){
            if (result.success) {
                console.log(result.data);
                ruleDetail = result.data;
                showRuleDetailPage(ruleDetail);
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function showRuleDetailPage(ruleDetail) {
    var page = $("#ruleDetailPage");
    page.empty();
    var skeleton = `
    <div class="row pb-2 d-flex">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#" onclick="showDailyInspectionPage()">Daily Inspection</a></li>
                <li class="breadcrumb-item" aria-current="page">${ruleId}</li>
            </ol>
        </nav>
        <h5 class="pb-3 fs-3">${ruleDetail.basicAttrs.name}</h5>
        <div class="row pb-2" id="ruleDetailBody">
        </div>
    </div>`;
    page.append(skeleton);
    
    var content = `
    <h4 class="pb-2 fs-4">Basic Information</h4>
      <div class="row pb-2">
        <p class="col-2 fw-semibold">
          Rule ID
        </p>
        <p class="col-4">
          ${ruleDetail.basicAttrs.id}
        </p>
        <p class="col-2 fw-semibold">
          Rule Name
        </p>
        <p class="col-4">
          ${ruleDetail.basicAttrs.name}
        </p>
        <p class="col-2 fw-semibold">
          Mainstay
        </p>
        <p class="col-4">
          ${ruleDetail.basicAttrs.mainstay}
        </p>
        <p class="col-2 fw-semibold">
          Create Time
        </p>
        <p class="col-4">
          ${ruleDetail.basicAttrs.createTime}
        </p>
        <p class="col-2 fw-semibold">
          Risk Level
        </p>
        <p class="col-4">
          ${riskLevelWords[parseInt(ruleDetail.basicAttrs.riskLevel) - 1]}
        </p>
        <p class="col-2 fw-semibold">
          Description
        </p>
        <p class="col-4">
          ${ruleDetail.basicAttrs.description}
        </p>
      </div>
      <h4 class="pb-2 fs-4">Inspection Result</h4>
      <div class="row d-flex">
        <div class="col-4">
        <select class="form-select" aria-label="Compliance select" id="complianceSelect" onchange="filterInspectionResultByCompliance()">
          <option value="ALL">Compliance status: All</option>
          <option value="COMPLIANT">Compliance status: Compliant</option>
          <option value="NON_COMPLIANT" selected>Compliance status: Non-compliant</option>
          <option value="NOT_APPLICABLE">Compliance status: Not applicable</option>
          <option value="INSUFFICIENT_DATA">Compliance status: Insufficient data</option>
        </select>
        </div>
      </div>
      <table class="table mt-2" id=inspection-result-table>
        <thead class="table-light">
        <tr>
            <th scope="col">Resource ID/Name</th>
            <th scope="col">Resource Type</th>
            <th scope="col">Compliance</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
      </table>`;
      $("#ruleDetailBody").append(content);
      filterInspectionResultByCompliance();
}

// 选择特定合规类型的资源
function filterInspectionResultByCompliance() {
  var compliance = $("#complianceSelect").val();
  $("#inspection-result-table tbody").empty();
  ruleDetail.inspectionResult.forEach(function(resource) {
    if (compliance === "ALL" || resource.compliance === compliance) {
      var row = `
      <tr>
          <td>
            <div><a target="_blank" class="text-decoration-none" href="../inspection/signInResourceInfo.do?resourceId=${resource.id}&resourceType=${resource.resourceType}&regionId=${resource.regionId}">${resource.id}</a></div>
            <div>${resource.name}</div>
          </td>
          <td>${resource.resourceType}</td>
          <td>${resource.compliance === "COMPLIANT" ? 
          `<span class="badge text-bg-success">${resource.compliance}</span>` : 
          resource.compliance === "NON_COMPLIANT" ? 
          `<span class="badge text-bg-danger">${resource.compliance}</span>` : 
          `<span class="badge text-bg-secondary">${resource.compliance}</span>`}</td>
      </tr>`;
      $("#inspection-result-table tbody").append(row);
    }
  });
}

// 返回daily inspection page
function showDailyInspectionPage() {
    var page = "daily_inspection/daily_inspection.html";
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}

function getDocumentByModule(){
    var params = {
      module: "dailyinspection"
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