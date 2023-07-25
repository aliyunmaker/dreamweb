var rules;

$(document).ready(function() {
    $("#ruleDetailPage").append(
    `<div class="position-absolute top-50 start-50 translate-middle">
        <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
            <span class="visually-hidden">Loading...</span>
        </div>
    </div>`
    );
    $.ajax({
        url: "../inspection/getRuleDetail.do",
        success: function(result){
            if (result.success) {
                console.log(result.data);
                rule = result.data;
                showRuleDetailPage(rule);
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function showRuleDetailPage(rule) {
    var page = $("#ruleDetailPage");
    page.empty();
    var skeleton = `
    <div class="row pb-2 d-flex">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#" onclick="showDailyInspectionPage()">Daily Inspection</a></li>
                <li class="breadcrumb-item" aria-current="page">Rule detail</li>
            </ol>
        </nav>
        <h5 class="pb-3 fs-2">Resources - ${appName}</h5>
        <div class="row pb-2" id="appDetailBody">
        </div>
    </div>`;
    page.append(skeleton);
    listAccounts();
    createCards(accountName);
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