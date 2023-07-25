var rules;
var ruleIds = "";

$(document).ready(function() {
    $("#dailyInspectionPage").append(
    `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );
    $.ajax({
        url: "../inspection/listRules.do",
        success: function(result){
            if (result.success) {
                console.log(result.data);
                rules = result.data;
                showDailyInspectionPage(rules);
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function showDailyInspectionPage(rules) {
    var page = $("#dailyInspectionPage");
    page.empty();
    var skeleton = `
    <div class="row pb-2 d-flex">
        <h5 class="pb-3 fs-2">Config Rules</h5>
        <div class="col">
            <button type="button" class="btn btn-success mr-2" onclick="activateRules()">Activate Rules</button>
            <button type="button" class="btn btn-danger" onclick="deactivateRules()">Deactivate Rules</button>
        </div>
        <div class="row pb-2 mt-2" id="dailyInspectionBody">
        </div>
    </div>`;
    page.append(skeleton);
    listRules(rules);
    addModals();
}

function listRules(rules) {
    var table = `
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Rule Name</th>
            <th scope="col">Compliance</th>
            <th scope="col">State</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>`;
    $("#dailyInspectionBody").append(table);
    for (var rule of rules) {
        ruleIds += rule.id + ","; // 拼接作为activate和deactivate的参数
        var row = `
        <tr>
            <td><a href=# class="text-decoration-none" onclick="getRuleDetail('${rule.id}')">${rule.name}</a></td>
            <td>${rule.compliance.complianceType === "COMPLIANT" ? 
                `<span class="badge text-bg-success">COMPLIANT(${rule.compliance.count})</span>` : 
                `<span class="badge text-bg-danger">NON-COMPLIANT(${rule.compliance.count})</span>`}</td>
            <td>${rule.state === "ACTIVE" ?
                `<p class="text-dark">ACTIVE</p>` :
                `<p class="text-secondary">INACTIVE</p>`}</td>
        </tr>`;
        $("#dailyInspectionBody tbody").append(row);
    }
    ruleIds = ruleIds.substring(0, ruleIds.length-1);
    console.log(ruleIds);
}

function addModals() {
    var activateModal = `
    <div class="modal fade" id="activateModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
            <h1 class="modal-title fs-5" id="exampleModalLabel">Activate Rules</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="activateModalBody" style="word-wrap: break-word;">All the rules are activated!</div>
            <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick=location.reload();>Close</button>
            </div>
        </div>
        </div>
    </div>`;
    $("#dailyInspectionBody").append(activateModal);
    var deactivateModal = `
    <div class="modal fade" id="deactivateModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
            <h1 class="modal-title fs-5" id="exampleModalLabel">Deactivate Rules</h1>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="deactivateModalBody" style="word-wrap: break-word;">All the rules are deactivated!</div>
            <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick=location.reload();>Close</button>
            </div>
        </div>
        </div>
    </div>`;
    $("#dailyInspectionBody").append(deactivateModal);
}

function activateRules() {
    var params = {
        ruleIds: ruleIds
    }
    $.ajax({
        url: "../" + "inspection/activateRules.do",
        data: params,
        type: "POST",
        success: function (result) {
          if (result.success) {
            $("#activateModal").modal('show');
          } else {
            console.log("data.message: " + result.errorMsg);
            alert(result.errorMsg);
          }
        }
    })
}

function deactivateRules() {
    var params = {
        ruleIds: ruleIds
    }
    $.ajax({
        url: "../" + "inspection/deactivateRules.do",
        data: params,
        type: "POST",
        success: function (result) {
          if (result.success) {
            $("#deactivateModal").modal('show');
          } else {
            console.log("data.message: " + result.errorMsg);
            alert(result.errorMsg);
          }
        }
    })
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