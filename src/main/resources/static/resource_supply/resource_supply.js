$(document).ready(function() {
   // 单选按钮点击事件
   $('input[type=radio][name=resourceType]').change(function () {
     var resourceType = $("input[name='resourceType']:checked").val();
     if (resourceType == "ecs") {
       $("#ecsTemplate").show();
       $("#ossTemplate").hide();
       $("#logTemplate").hide();
       $("#ecsResoureProperties").show();
       $("#ossResoureProperties").hide();
       $("#logResoureProperties").hide();
     } else if (resourceType == "oss") {
       $("#ecsTemplate").hide();
       $("#ossTemplate").show();
       $("#logTemplate").hide();
       $("#ecsResoureProperties").hide();
       $("#ossResoureProperties").show();
       $("#logResoureProperties").hide();
     } else if (resourceType == "log") {
       $("#ecsTemplate").hide();
       $("#ossTemplate").hide();
       $("#logTemplate").show();
       $("#ecsResoureProperties").hide();
       $("#ossResoureProperties").hide();
       $("#logResoureProperties").show();
     }
   });

   $('input[type=radio][name=environmentType]').change(function () {
     getVSwitches();
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

function getVSwitches() {
  var application = $("#application").val();
  var environmentType = $("input[name='environmentType']:checked").val();


  var params = {
    applicationName: application,
    environmentName: environmentType,
  };

  $.ajax({
    url: "../" + "resourceSupply/getVSwitches.do",
    type: "POST",
    data: params,
    contentType: "application/x-www-form-urlencoded",
    success: function (result) {
      if (result.success) {
        var vSwitch = document.getElementById("vSwitch");
        for (var i = vSwitch.length - 1; i >= 1; i--) {
          vSwitch.remove(i);
        }
        data = result.data;
        for (var i = 0; i < data.length; i++) {
          var vSwitchId = data[i].split("/")[1].trim();
          console.log("vSwitchId: " + vSwitchId);
          vSwitch.options.add(new Option(data[i], vSwitchId));
        }
        vSwitch.value = "";
      } else {
        console.log("result.errorMsg: " + result.errorMsg);
        alert(result.errorMsg);
      }
    },
  })
}

function getTemplateByResourceType() {
  var resourceType = $("input[name='resourceType']:checked").val();
  console.log("resourceType: " + resourceType);

  var params = {
    resourceType: resourceType
  };

  $.ajax({
    url: "../" + "resourceSupply/getTemplateByResourceType.do",
    type: "POST",
    data: params,
    success: function (result) {
      if (result.success) {
        let len = result.data.split("\n").length;
        console.log(len);
        $("#templateContent").attr("rows", len);
        $("#templateContent").val(result.data);
      } else {
        console.log("result.errorMsg: " + result.errorMsg);
        alert(result.errorMsg);
      }
    },
  })
}

function createResource() {
  var resourceType = $("input[name='resourceType']:checked").val();
  $("#createFail").hide();
  $("#createFailMessage").hide();
  $("#createSuccess").hide();
  $("#creating").show();
  if (resourceType == "ecs") {
    createEcsInstance();
  } else if (resourceType == "oss") {
    createOssBucket();
  } else if (resourceType == "log") {
    createLogProject();
  }
  return false;
}

function createEcsInstance() {
  var regionId = $("#regionId").val();
  var vSwitchId = $("#vSwitch").val();
  var instanceType = $("#instanceType").val();
  var amount = $("#instanceNum").val();
  var application = $("#application").val();
  var environmentType = $("input[name='environmentType']:checked").val();

  var params = {
    applicationName: application,
    environmentName: environmentType,
    regionId: regionId,
    vSwitchId: vSwitchId,
    instanceType: instanceType,
    amount: amount
  };

  $.ajax({
    url: "../" + "resourceSupply/createEcsInstance.do",
    type: "POST",
    data: params,
    success: function (result) {
      $("#creating").hide();
      if (result.success) {
        $("#createSuccess").show();
      } else {
        $("#createFail").show();
        $("#createFailMessage").show();
        $("#createFailMessageContent").text(result.errorMsg);
      }
    },
  })
}

function createOssBucket() {
  var bucketName = $("#bucketName").val();
  var application = $("#application").val();
  var environmentType = $("input[name='environmentType']:checked").val();

  var params = {
    bucketName: bucketName,
    applicationName: application,
    environmentName: environmentType
  };

  $.ajax({
    url: "../" + "resourceSupply/createOssBucket.do",
    type: "POST",
    data: params,
    success: function (result) {
      $("#creating").hide();
      if (result.success) {
        $("#createSuccess").show();
      } else {
        $("#createFail").show();
        $("#createFailMessage").show();
        $("#createFailMessageContent").text(result.errorMsg);
      }
    },
  })
}

function createLogProject() {
  var projectName = $("#projectName").val();
  var description = $("#projectDescription").val();
  var application = $("#application").val();
  var environmentType = $("input[name='environmentType']:checked").val();

  var params = {
    projectName: projectName,
    description: description,
    applicationName: application,
    environmentName: environmentType
  };

  $.ajax({
    url: "../" + "resourceSupply/createLogProject.do",
    type: "POST",
    data: params,
    success: function (result) {
      $("#creating").hide();
      if (result.success) {
        $("#createSuccess").show();
      } else {
        $("#createFail").show();
        $("#createFailMessage").show();
        $("#createFailMessageContent").text(result.errorMsg);
      }
    },
  })
}