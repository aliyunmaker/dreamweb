$(document).ready(function() {
  // 单选按钮点击事件
  $('input[type=radio][name=environmentType]').change(function () {
    listResourcesByAppEnvAndResType();
  });

  $('input[type=radio][name=permissionTemplate]').change(function () {
    generatePolicyDocument();
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
    deleteAccessKeyRequest = new com.aliyun.ram20150501.models.DeleteAccessKeyRequest()
      .setUserName("aaa")
      .setUserAccessKeyId("111"); links.forEach(function (link) {
        link.classList.remove("active");
      });

    // Add active class to the clicked link
    e.target.classList.add("active");
  });
});

function akApplySubmit() {
  application = document.getElementById("application").value;
  environmentType = $("input[name='environmentType']:checked").val();
  policyDocument = document.getElementById("policyDocument").value;
  var params = {
    applicationName: application,
    environment: environmentType,
    policyDocument: policyDocument
  };

  $.ajax({
    url: "../" + "akApply/akApplySubmit.do",
    type: "POST",
    data: params,
    async: false,
    success: function (result) {
      if (result.success) {
        document.getElementById("accessKeyIdShow").innerText = result.data.accessKeyId;
        document.getElementById("accessKeySecretShow").innerText = result.data.accessKeySecret;
        $('#myModal').modal('show');
      } else {
        console.log("data.message: " + result.errorMsg);
        document.getElementById("failMessage").innerText = result.errorMsg;
        $('#modalFail').modal('show');
      }
    },
  });
  return false;
}

function generatePolicyDocument() {
  applyResourceType = document.getElementById("applyResourceType").value;
  concreteResource = document.getElementById("concreteResource").value;
  permissionTemplate = $("input[name='permissionTemplate']:checked").val();
  if (applyResourceType == "" || concreteResource == "" || permissionTemplate == undefined) {
    return;
  }

  // // 测试
  // concreteResource = "buckettestjia";
  var params = {
    resourceType: applyResourceType,
    resourceName: concreteResource,
    actionCode: permissionTemplate
  };

  $.ajax({
    url: "../" + "akApply/generatePolicyDocument.do",
    type: "POST",
    data: params,
    success: function (data) {
      document.getElementById("policyDocument").value = data;
    },
  })
}

function listResourcesByAppEnvAndResType() {
  application = document.getElementById("application").value;
  environmentType = $("input[name='environmentType']:checked").val();
  applyResourceType = document.getElementById("applyResourceType").value;
  if (application == "" || environmentType == undefined || applyResourceType == "") {
    return;
  }
  var params = {
    applicationName: application,
    environment: environmentType,
    resourceType: applyResourceType
  };

  var concreteResource = document.getElementById("concreteResource");
  for (var i = concreteResource.length - 1; i >= 1; i--) {
    concreteResource.remove(i);
  }


  $.ajax({
    url: "../" + "akApply/listResourcesByAppEnvAndResType.do",
    type: "POST",
    data: params,
    success: function (result) {
      if (result.success) {
        var concreteResource = document.getElementById("concreteResource");
        var data = result.data;
        for (var i = concreteResource.length - 1; i >= 1; i--) {
          concreteResource.remove(i);
        }
        for (var i = 0; i < data.length; i++) {
          concreteResource.options.add(new Option(data[i], data[i]));
        }
        concreteResource.value = "";
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    },
  })
}