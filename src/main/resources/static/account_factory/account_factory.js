var folderId;
var masterAccountName;
var masterAccountId;
var accountNameSuffix;
var baselines;
var cloudProudct = {
  "CEN_TR": "云企业网CEN-转发路由服务",
  "PVTZ": "云解析 PrivateZone",
  "CDN": "内容分发网络CDN",
  "DCDN": "全站加速",
  "PRIVATE_LINK": "私网连接 PrivateLink",
  "KMS": "密钥管理服务KMS",
  "NAT": "NAT网关",
  "OSS": "对象存储OSS",
  "NAS": "文件存储NAS",
  "OTS": "表格存储Tablestore",
  "LOG": "日志服务",
  "PROMETHEUS": "Prometheus监控服务",
  "SLB_ACCESS_LOG": "SLB-访问日志",
  "SLB_HEALTH_LOG_STORAGE": "SLB-健康检测日志存储",
  "SLB_HEALTH_DIAGNOSE": "SLB-健康检查探测",
  "TAG_CREATEDBY": "创建者标签",
  "SERVICE_CATALOG": "服务目录",
  "DBS": "数据库备份DBS",
  "DMS": "数据管理",
  "DTS": "数据传输",
  "ARMS": "应用实时监控服务",
  "CMS_NAAM": "云监控-网络分析与监控",
  "MNS": "消息服务MNS",
  "FC": "函数计算",
  "API_GATEWAY": "API网关",
  "CMS": "云监控-基础云监控",
  "CONFIG": "配置审计",
  "SAS": "云安全中心（态势感知）",
  "CDT": "云数据传输-VPC Peering-按流量计费（非跨境）",
  "ACK_PRO": "容器服务Kubernetes版",
}
$(document).ready(function () {
  $("#loading").removeClass("d-none");

  getAccountNameSuffix();
  getFileTree();
  getMasterAccount();
  getBaselines();

   // 定时检查
   var intervalId = setInterval(function() {
    if (masterAccountId != null && accountNameSuffix != null && baselines != null) {
      $("#loading").addClass("d-none");
      $("#content").show();
      clearInterval(intervalId); // 停止定时检查
    }
  }, 500); // 0.5s检查一次


});

function getBaselines() {
  $.ajax({
    url: "../" + "accountFactory/getBaselines.do",
    type: "POST",
    // async: false,
    data: {},
    success: function (result) {
      if (result.success) {
        baselines = result.data;
        for (var i = 0; i < baselines.length; i++) {
          var baselineName = baselines[i].split("/")[0];
          var baselineId = baselines[i].split("/")[1];
          if (i == 0) {
            $("#baselinesPage").append(
              `<div class="form-group row">
              <label for="baseline1" class="col-sm-2 col-form-label">基线选择：</label>
              <div class="col-sm-10">
                  <input type="radio" class="form-check-input" id="baseline${i + 1}" name="baseline" value="${baselineId}" checked>
                  <label class="form-check-label" for="baseline${i + 1}">${baselineName}</label>
                  <a href="#offcanvasRight" role="button" data-bs-toggle="offcanvas" aria-controls="offcanvasRight"
                     style="text-decoration: none;" onclick="getBaselineDetails('${baselineId}')">查看模板</a>
              </div>
          </div>`);
          } else {
            $("#baselinesPage").append(
              `<div class="form-group row">
              <label for="baseline1" class="col-sm-2 col-form-label"></label>
              <div class="col-sm-10">
                  <input type="radio" class="form-check-input" id="baseline${i + 1}" name="baseline" value="${baselineId}">
                  <label class="form-check-label" for="baseline${i + 1}">${baselineName}</label>
                  <a href="#offcanvasRight" role="button" data-bs-toggle="offcanvas" aria-controls="offcanvasRight"
                     style="text-decoration: none;" onclick="getBaselineDetails('${baselineId}')">查看模板</a>
              </div>
          </div>`);
          }


        }

      } else {
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
    // async: false,
    data: {},
    success: function (result) {
      if (result.success) {
        masterAccountName = result.data.split("/")[0];
        masterAccountId = result.data.split("/")[1];
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
    // async: false,
    data: {},
    success: function (result) {
      if (result.success) {
        accountNameSuffix = result.data;
        document.getElementById("accountNameSuffix").textContent = accountNameSuffix;
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}

function createCloudAccount() {
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

function getBaselineDetails(baselineId) {
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
        var baselineItems = baselineDetails.baselineItems;
        if (baselineDetails.name == "基础基线") {
          showbaselineDetailPage(baselineItems);
        } else if (baselineDetails.name == "网络基线") {
          showNetworkBaselineDetail(baselineDetails);
        } else {
          console.log("baselineDetails.name: " + baselineDetails.name);
          alert("暂不支持该基线");
        }
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    }
  })
}

function showbaselineDetailPage(baselineItems) {
  $(".offcanvas-body").empty();
  for (var i = 0; i < baselineItems.length; i++) {
    var config = baselineItems[i].config;
    var itemName = baselineItems[i].itemName;
    //    console.log("config: " + config);
    //    console.log("itemName: " + itemName);
    if (itemName == "ACS-BP_ACCOUNT_FACTORY_ACCOUNT_CONTACT") {
      console.log("config: " + config);
      console.log("itemName: " + itemName);
      showAccountContact(config);
    }

    if (itemName == "ACS-BP_ACCOUNT_FACTORY_ACCOUNT_NOTIFICATION") {
      console.log("config: " + config);
      console.log("itemName: " + itemName);
      showAccountNotification(config);
    }

    if (itemName == "ACS-BP_ACCOUNT_FACTORY_SUBSCRIBE_SERVICES") {
      console.log("config: " + config);
      console.log("itemName: " + itemName);
      showSubscribeServices(config);
    }

    if (itemName == "ACS-BP_ACCOUNT_FACTORY_RAM_USER_PASSWORD_POLICY") {
      console.log("config: " + config);
      console.log("itemName: " + itemName);
      showRamUserPasswordPolicy(config);
    }

    if (itemName == "ACS-BP_ACCOUNT_FACTORY_PRESET_TAG") {
      console.log("config: " + config);
      console.log("itemName: " + itemName);
      showPresetTag(config);
    }

    if (itemName == "ACS-BP_ACCOUNT_FACTORY_RAM_ROLE") {
      console.log("config: " + config);
      console.log("itemName: " + itemName);
      showRamRole(config);
    }
  }

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
            <h4 class="card-title mb-4">${baselineItem.itemName === "ACS-BP_ACCOUNT_FACTORY_VPC"? "VPC": "Security Group"}</h4>
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
          $("#card-" + config.Name + " .card-body").append(cardBodyContent);

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
            <h6 class="card-subtitle mb-2">Ingress ACL Entries</h6>
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
            <h6 class="card-subtitle mb-2">Egress ACL Entries</h6>
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
            $("#card-" + config.Name + " #acls").append(acl);

            for (var i in networkAcl.IngressAclEntries) {
              var row = `
              <tr>
                <td>${parseInt(i) + 1}</td>
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
                <td>${parseInt(i) + 1}</td>
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

          $("#card-" + config.Name + " .card-body").append(cardBodyContent);

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

function showAccountContact(config) {
  var title = "账号联系人"
  var col1 = "姓名";
  var col2 = "邮箱";
  var col3 = "手机";
  var col4 = "职位";
  var htmlContent = `<div class="card mb-3" style="width: 100%;">
        <div class="card-body">
            <h5 class="card-title mb-3">${title}</h5>
            <table class="table">
                <thead class="table-light">
                    <tr>
                        <th scope="col">${col1}</th>
                        <th scope="col">${col2}</th>
                        <th scope="col">${col3}</th>
                        <th scope="col">${col4}</th>
                    </tr>
                </thead>
                <tbody>`;
  var contacts = config.Contacts;
  for (var j = 0; j < contacts.length; j++) {
    var email = contacts[j].Email;
    var position = contacts[j].Position;
    var mobile = contacts[j].Mobile;
    var name = contacts[j].Name;
    htmlContent += `<tr>
          <td>${name}</th>
          <td>${email}</td>
          <td>${mobile}</td>
          <td>${position}</td>
      </tr>`;
  }
  htmlContent += `</tbody>
        </table>
    </div>
</div>`;
  $("#baselineDetailPage").append(htmlContent);
}

function showSubscribeServices(config) {
  var title = "开通云产品";
  var lable1 = "开通列表"
  var enabledServices = config.EnabledServices;
  var enabledServicesDisplay = "";
  for (var i = 0; i < enabledServices.length; i++) {
    enabledServicesDisplay += cloudProudct[enabledServices[i]] + "，";
  }
  enabledServicesDisplay = enabledServicesDisplay.substring(0, enabledServicesDisplay.length - 1);
  var htmlContent = ` <div class="card mb-3" style="width: 100%;">
  <div class="card-body">
      <h5 class="card-title mb-3">${title}</h5>
      <div class="row">
          <label class="col-sm-2" style="font-weight: bold;">${lable1}</label>
          <div class="col-sm-10">
              <p>${enabledServicesDisplay}</p>
          </div>
      </div>
  </div>
</div>`;
  $("#baselineDetailPage").append(htmlContent);
}

function showRamUserPasswordPolicy(config) {
  var title = "RAM密码策略";
  var label1 = "密码长度";
  var label2 = "密码中必须包含元素";
  var label3 = "密码有效期";
  var label4 = "密码过期后不可登录";
  var label5 = "历史密码检查策略";
  var label6 = "密码重试约束";
  var value1 = config.MinimumPasswordLength + " ~ 32";
  var value2 = "";
  if (config.RequireLowercaseCharacters == true) {
    value2 += "小写字母，";
  }
  if (config.RequireUppercaseCharacters == true) {
    value2 += "大写字母，";
  }
  if (config.RequireSymbols == true) {
    value2 += "符号，";
  }
  if (config.RequireNumbers == true) {
    value2 += "数字，";
  }
  value2 = value2.substring(0, value2.length - 1);
  var value3 = config.MaxPasswordAge + " 天 ";
  var value4 = "";
  if (config.HardExpiry == true) {
    value4 = "是";
  } else {
    value4 = "否";
  }
  var value5 = "禁止使用前" + config.PasswordReusePrevention + "次密码";
  var value6 = "一小时内使用错误密码最大尝试" + config.MaxLoginAttempts + "次登录";

  var htmlContent = ` <div class="card mb-3" style="width: 100%;">
  <div class="card-body">
      <h5 class="card-title mb-3">${title}</h5>
      <div class="row mb-1">
          <label class="col-sm-2" style="font-weight: bold;">${label1}</label>
          <div class="col-sm-4">
              <p>${value1}</p>
          </div>
          <label class="col-sm-2" style="font-weight: bold;">${label2}</label>
          <div class="col-sm-4">
              <p>${value2}</p>
          </div>
      </div>
      <div class="row mb-1">
          <label class="col-sm-2" style="font-weight: bold;">${label3}</label>
          <div class="col-sm-4">
              <p>${value3}</p>
          </div>
          <label class="col-sm-2" style="font-weight: bold;">${label4}</label>
          <div class="col-sm-4">
              <p>${value4}</p>
          </div>
      </div>
      <div class="row mb-1">
          <label class="col-sm-2" style="font-weight: bold;">${label5}</label>
          <div class="col-sm-4">
              <p>${value5}</p>
          </div>
          <label class="col-sm-2" style="font-weight: bold;">${label6}</label>
          <div class="col-sm-4">
              <p>${value6}</p>
          </div>
      </div>
  </div>
</div>`;
  $("#baselineDetailPage").append(htmlContent);

}



function showAccountNotification(config) {
  var title = "消息通知";
  var col1 = "通知类型";
  var col2 = "联系方式";
  var col3 = "联系人";
  var htmlContent = `<div class="card mb-3" style="width: 100%;">
        <div class="card-body">
            <h5 class="card-title mb-3">${title}</h5>
            <table class="table">
                <thead class="table-light">
                    <tr>
                        <th scope="col">${col1}</th>
                        <th scope="col">${col2}</th>
                        <th scope="col">${col3}</th>
                    </tr>
                </thead>
                <tbody>`;
  var notifications = config.Notifications;
  for (var j = 0; j < notifications.length; j++) {
    var groupKey = notifications[j].GroupKey;
    var notificationType = "";
    if (groupKey == "account_msg") {
      notificationType = "账户资金相关信息";
    } else if (groupKey == "security_msg") {
      notificationType = "安全消息";
    } else if (groupKey == "gztz") {
      notificationType = "故障消息";
    }
    var contacts = notifications[j].Contacts;
    var contact = "";
    for (var k = 0; k < contacts.length; k++) {
      contact += contacts[k].Name + " ";
    }
    var contactMethod = "";
    if (notifications[j].EmailStatus == "1") {
      contactMethod += "邮箱，";
    }
    if (notifications[j].PmsgStatus == "1") {
      contactMethod += "站内信，";
    }
    if (notifications[j].SmsStatus == "1") {
      contactMethod += "短信，";
    }
    contactMethod = contactMethod.substring(0, contactMethod.length - 1);

    htmlContent += `<tr>
          <td>${notificationType}</th>
          <td>${contactMethod}</td>
          <td>${contact}</td>
      </tr>`;
  }
  htmlContent += `</tbody>
        </table>
    </div>
</div>`;
  $("#baselineDetailPage").append(htmlContent);
}


function showPresetTag(config) {
  var title = "预置标签";
  var col1 = "标签键";
  var col2 = "标签值";
  var htmlContent = `<div class="card mb-3" style="width: 100%;">
        <div class="card-body">
            <h5 class="card-title mb-3">${title}</h5>
            <table class="table">
                <thead class="table-light">
                    <tr>
                        <th scope="col">${col1}</th>
                        <th scope="col">${col2}</th>
                    </tr>
                </thead>
                <tbody>`;
  var presetTags = config.PresetTags;
  for (var j = 0; j < presetTags.length; j++) {
    var key = presetTags[j].Key;
    var values = presetTags[j].Values;
    var valuesDisplay = "";
    for (var k = 0; k < values.length; k++) {
      valuesDisplay += values[k] + ",";
    }
    valuesDisplay = valuesDisplay.substring(0, valuesDisplay.length - 1);

    htmlContent += `<tr>
          <td>${key}</th>
          <td>${valuesDisplay}</td>
      </tr>`;
  }
  htmlContent += `</tbody>
        </table>
    </div>
</div>`;
  $("#baselineDetailPage").append(htmlContent);
}

function showRamRole(config) {
  var title = "RAM角色";
  var col1 = "角色名称";
  var col2 = "角色授权";
  var col3 = "授信实体";
  var col4 = "最大会话时间";
  var htmlContent = `<div class="card mb-3" style="width: 100%;">
        <div class="card-body">
            <h5 class="card-title mb-3">${title}</h5>
            <table class="table">
                <thead class="table-light">
                    <tr>
                        <th scope="col">${col1}</th>
                        <th scope="col">${col2}</th>
                        <th scope="col">${col3}</th>
                        <th scope="col">${col4}</th>
                    </tr>
                </thead>
                <tbody>`;
  console.log("RamRoleConfig: ");
  console.log(config);
  var enableRoles = config.EnabledRoles;
  console.log("enableRoles: " + enableRoles);
  for (var j = 0; j < enableRoles.length; j++) {
    var name = enableRoles[j].Name;
    var durationSeconds = enableRoles[j].DurationSeconds;
    var systemPolicies = enableRoles[j].SystemPolicies;
    var systemPoliciesDisplay = "";
    for (var k = 0; k < systemPolicies.length; k++) {
      systemPoliciesDisplay += systemPolicies[k] + ",";
    }
    systemPoliciesDisplay = systemPoliciesDisplay.substring(0, systemPoliciesDisplay.length - 1);
    var trustedEntity = "当前企业管理账号（" + masterAccountId + ")";

    htmlContent += `<tr>
          <td>${name}</th>
          <td>${systemPoliciesDisplay}</td>
          <td>${trustedEntity}</td>
          <td>${durationSeconds}</td>
      </tr>`;
  }
  htmlContent += `</tbody>
        </table>
    </div>
</div>`;
  $("#baselineDetailPage").append(htmlContent);
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
