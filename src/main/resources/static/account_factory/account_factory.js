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
        var baselineDetails = result.data;
        console.log(baselineDetails);
        document.getElementById('baselineTitle').innerHTML = baselineDetails.name;
        if (baselineDetails.name === "网络基线") {
          showNetworkBaselineDetail(baselineDetails);
        } else {
        var baselineItems = baselineDetails.baselineItems;
        for(var i=0; i < baselineItems.length; i++){
          var config = baselineItems[i].config;
          var itemName = baselineItems[i].itemName;
          console.log("config: " + config);
            console.log("itemName: " + itemName);
          if(itemName == "ACS-BP_ACCOUNT_FACTORY_ACCOUNT_CONTACT"){
            console.log("config: " + config);
            console.log("itemName: " + itemName);
            $("baselineDetailPage").append(``);
          }

        }
      }

      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}

function showNetworkBaselineDetail(baselineDeatils) {
  // $("#baselineTitle").empty();
  // $("#baselineTitle").append(baselineDeatils.name+" / "+baselineDeatils.id);

  $(".offcanvas-body").empty();
  for (var baselineItem of baselineDeatils.baselineItems) {
    for (var configs in baselineItem.config) {
      for (var config of baselineItem.config[configs]) {
        var content = `
        <div class="card mb-4" id="card-${config.Name}">
          <div class="card-body">
            <h4 class="card-title mb-4">${baselineItem.itemName}</h4>
          </div>
        </div>`;
        $(".offcanvas-body").append(content);
        if (configs === "Vpcs") {
          var cardBodyContent = `
            <div class="row pb-2">
              <p class="col-2 fw-semibold">
                Region
              </p>
              <p class="col-4">
                ${config.RegionId}
              </p>
              <p class="col-2 fw-semibold">
                Name
              </p>
              <p class="col-4">
                ${config.Name}
              </p>
              <p class="col-2 fw-semibold">
                Cidr Block
              </p>
              <p class="col-4">
                ${config.CidrBlock}
              </p>
              <p class="col-2 fw-semibold">
                Description
              </p>
              <p class="col-4">
                ${config.Description}
              </p>
            </div>
            <div id="vswitches">
            <h5 class="card-subtitle mb-2">VSwitches</h5>
            <table class="table" id="vswitches-table">
              <thead>
              <tr>
                  <th scope="col">Name</th>
                  <th scope="col">Zone Id</th>
                  <th scope="col">Cidr Block</th>
              </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
            </div>
            <div id="acls">
            </div>
            `;
          $("#card-"+config.Name+" .card-body").append(cardBodyContent);
          
          for (var vswitch of config.VSwitches) {
            var row = `
            <tr>
              <td>${vswitch.Name}</td>
              <td>${vswitch.ZoneId}</td>
              <td>${vswitch.CidrBlock}</td>
            </tr>`;
            $("#vswitches-table tbody").append(row);
          }

          for (var networkAcl of config.NetworkAcls) {
            var acl = `
            <h5 class="card-subtitle mb-2">ACL</h5>
            <div class="row pb-2">
              <p class="col-2 fw-semibold">
                Name
              </p>
              <p class="col-4">
                ${networkAcl.Name}
              </p>
              <p class="col-2 fw-semibold">
                Description
              </p>
              <p class="col-4">
                ${networkAcl.Description}
              </p>
            </div>
            <h5 class="card-subtitle mb-2">Ingress ACL Entries</h5>
            <table class="table" id="ingress-acl-entries-table">
              <thead>
              <tr>
                  <th scope="col">Order of effect</th>
                  <th scope="col">Name</th>
                  <th scope="col">Protocol</th>
                  <th scope="col">Cidr IP</th>
                  <th scope="col">Port</th>
                  <th scope="col">Policy</th>
              </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
            <h5 class="card-subtitle mb-2">Egress ACL Entries</h5>
            <table class="table" id="egress-acl-entries-table">
              <thead>
              <tr>
                  <th scope="col">Order of effect</th>
                  <th scope="col">Name</th>
                  <th scope="col">Protocol</th>
                  <th scope="col">Cidr IP</th>
                  <th scope="col">Port</th>
                  <th scope="col">Policy</th>
              </tr>
              </thead>
              <tbody>
              </tbody>
            </table>`;
            $("#card-"+config.Name+" #acls").append(acl);

            for (var i in networkAcl.IngressAclEntries) {
              var row = `
              <tr>
                <td>${parseInt(i)+1}</td>
                <td>${networkAcl.IngressAclEntries[i].Name}</td>
                <td>${networkAcl.IngressAclEntries[i].Protocol}</td>
                <td>${networkAcl.IngressAclEntries[i].CidrIp}</td>
                <td>${networkAcl.IngressAclEntries[i].Port}</td>
                <td>${networkAcl.IngressAclEntries[i].Policy}</td>
              </tr>`;
              $("#ingress-acl-entries-table tbody").append(row);
            }
            for (var i in networkAcl.EgressAclEntries) {
              var row = `
              <tr>
                <td>${parseInt(i)+1}</td>
                <td>${networkAcl.EgressAclEntries[i].Name}</td>
                <td>${networkAcl.EgressAclEntries[i].Protocol}</td>
                <td>${networkAcl.EgressAclEntries[i].CidrIp}</td>
                <td>${networkAcl.EgressAclEntries[i].Port}</td>
                <td>${networkAcl.EgressAclEntries[i].Policy}</td>
              </tr>`;
              $("#egress-acl-entries-table tbody").append(row);
            }
          }
        } else if (configs === "SecurityGroups") {
          var cardBodyContent = `
          <div class="row pb-2">
            <p class="col-2 fw-semibold">
              Name
            </p>
            <p class="col-4">
              ${config.Name}
            </p>
            <p class="col-2 fw-semibold">
              Security Group Type
            </p>
            <p class="col-4">
              ${config.Type}
            </p>
            <p class="col-2 fw-semibold">
              VPC
            </p>
            <p class="col-4">
              ${config.Vpc.Name}
            </p>
          </div>
          <div id="ingress">
            <h5 class="card-subtitle mb-2">Ingress</h5>
            <table class="table" id="ingress-table">
              <thead>
              <tr>
                  <th scope="col">Policy</th>
                  <th scope="col">Priority</th>
                  <th scope="col">IP Protocol</th>
                  <th scope="col">PortRanges</th>
                  <th scope="col">Cidr IPs</th>
                  <th scope="col">Description</th>
              </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
          </div>
          <div id="egress">
            <h5 class="card-subtitle mb-2">Egress</h5>
            <table class="table" id="egress-table">
              <thead>
              <tr>
                  <th scope="col">Policy</th>
                  <th scope="col">Priority</th>
                  <th scope="col">IP Protocol</th>
                  <th scope="col">Port Ranges</th>
                  <th scope="col">Cidr IPs</th>
                  <th scope="col">Description</th>
              </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
          </div>`;

          $("#card-"+config.Name+" .card-body").append(cardBodyContent);

          for (var rule of config.Rules) {
            var row = `
            <tr>
              <td>${rule.Policy}</td>
              <td>${rule.Priority}</td>
              <td>${rule.IpProtocol}</td>
              <td>${rule.PortRanges}</td>
              <td>${rule.CidrIps}</td>
              <td>${rule.Description}</td>
            </tr>`;
            if (rule.Type === "ingress") {
              $("#ingress-table tbody").append(row);
            } else if (rule.Type === "egress") {
              $("#egress-table tbody").append(row);
            }
          }
        }
        
      }
    }
  }
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
