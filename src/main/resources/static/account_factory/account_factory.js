var folderId;
$(document).ready(function () {
  getAccountNameSuffix();
  getFileTree();
  getMasterAccount();
  getBaselines();

});

function getBaselines(){
  $.ajax({
    url: "../" + "accountFactory/getBaselines.do",
    type: "POST",
    data: {},
    success: function (result) {
      if (result.success) {
        var baselines = result.data;
        for(var i=0; i<baselines.length; i++){
          baselineName = baselines[i].split("/")[0];
          baselineId = baselines[i].split("/")[1];
          if(i == 0){
            $("#baselinesPage").append(
              `<div class="form-group row">
              <label for="baseline1" class="col-sm-2 col-form-label">基线选择：</label>
              <div class="col-sm-10">
                  <input type="radio" class="form-check-input" id="baseline${i+1}" name="baseline" value="${baselineId}" checked>
                  <label class="form-check-label" for="baseline${i+1}">${baselineName}</label>
                  <a href="#offcanvasRight" role="button" data-bs-toggle="offcanvas" aria-controls="offcanvasRight"
                     style="text-decoration: none;" onclick="getBaselineDetails('${baselineId}')">查看模板</a>
              </div>
          </div>`);
          }else{
            $("#baselinesPage").append(
              `<div class="form-group row">
              <label for="baseline1" class="col-sm-2 col-form-label"></label>
              <div class="col-sm-10">
                  <input type="radio" class="form-check-input" id="baseline${i+1}" name="baseline" value="${baselineId}">
                  <label class="form-check-label" for="baseline${i+1}">${baselineName}</label>
                  <a href="#offcanvasRight" role="button" data-bs-toggle="offcanvas" aria-controls="offcanvasRight"
                     style="text-decoration: none;" onclick="getBaselineDetails('${baselineId}')">查看模板</a>
              </div>
          </div>`);
          }


        }

      }else{
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}

function getMasterAccount() {
  $.ajax({
    url: "../" + "accountFactory/getMasterAccount.do",
    type: "POST",
    data: {},
    success: function (result) {
      if (result.success) {
        var masterAccountName = result.data.split("/")[0];
        var masterAccountId = result.data.split("/")[1];
        var text = document.getElementById("paymentMethod1Desc").textContent;
        document.getElementById("paymentMethod1Desc").textContent = text + " (" + masterAccountName + ")";
        document.getElementById("paymentMethod1").value = masterAccountId;
        console.log("masterAccountName: " + document.getElementById("paymentMethod1Desc").textContent);
        console.log("masterAccountId: " + document.getElementById("paymentMethod1").value);
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}


// 初始化账号资源夹
function getFileTree() {
  var treeData;
  $.ajax({
    url: "../" + "accountFactory/getFileTree.do",
    type: "POST",
    data: {},
    success: function (result) {
      if (result.success) {
        treeData = result.data;
        // console.log("treeData: " + treeData);
        $('#treeview').treeview({
          data: treeData,
          expanded: false,//初始是否展开
          levels: 2,//初始显示层数
          icon: "iconfont icon-resource",
          nodeID: "id",
          onNodeSelected: function (event, node) {
            folderId = node.id;
            $('#flush-collapseOne').collapse('toggle');
            document.getElementById("folderName").innerHTML = node.text;
          },
          expandIcon: 'iconfont icon-file',  // 展开图标
          collapseIcon: 'iconfont icon-file',  // 折叠图标
          emptyIcon: 'iconfont icon-file',   // 空图标
        });
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
  return treeData;
}

// 获取账号名称后缀
function getAccountNameSuffix() {
  $.ajax({
    url: "../" + "accountFactory/getAccountNameSuffix.do",
    type: "POST",
    data: {},
    success: function (result) {
      if (result.success) {
        var accountNameSuffix = result.data;
        document.getElementById("accountNameSuffix").textContent = accountNameSuffix;
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}

function createCloudAccount(){
  $("#createFail").hide();
  $("#createFailMessage").hide();
  $("#createSuccess").hide();
  $("#creating").show();
  var accountNamePrefix = document.getElementById("accountName").value;
  var displayName = document.getElementById("memberName").value;
  var payerAccountUid = $("input[name='paymentMethod']:checked").val();
  // var accountUid = ;
  var baselineId = $("input[name='baseline']:checked").val();
  // var resellAccountType = ;
  console.log("accountNamePrefix: " + accountNamePrefix);
  console.log("displayName: " + displayName);
  console.log("payerAccountUid: " + payerAccountUid);
  console.log("baselineId: " + baselineId);
  var params = {
    accountNamePrefix: accountNamePrefix,
    displayName: displayName,
    folderId: folderId,
    payerAccountUid: payerAccountUid,
    baselineId: baselineId,
  }
  $.ajax({
    url: "../" + "accountFactory/createCloudAccount.do",
    type: "POST",
    data: params,
    success: function (result) {
      $("#creating").hide();
      if (result.success) {
        var accountUid = result.data;
        console.log("accountUid: " + accountUid);
        document.getElementById("successMessage").textContent = "accountUid" + "\xa0:\xa0" + accountUid;
        $("#createSuccess").show();
      } else {
        console.log("data.message: " + result.errorMsg);
        $("#createFail").show();
        document.getElementById("failMessage").textContent = result.errorMsg;
//        alert(result.errorMsg);
      }
    }
  })

  return false;
}

function getBaselineDetails(baselineId){
  var params = {
    baselineId: baselineId
  }
  $.ajax({
    url: "../" + "accountFactory/getBaselineDetails.do",
    type: "POST",
    data: params,
    success: function (result) {
      if (result.success) {
        var baselineDeatils = result.data;



      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}


function getDocumentByModule() {
  var params = {
    module: "accountfactory"
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
