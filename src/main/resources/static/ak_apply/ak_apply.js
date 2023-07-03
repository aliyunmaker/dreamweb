var editor;
$(document).ready(function() {
    // 单选按钮点击事件
    $('input[type=radio][name=environmentType]').change(function () {
    listResourcesByAppEnvAndResType();
    });

    $('input[type=radio][name=permissionTemplate]').change(function () {
    generatePolicyDocument();
    });

    //根据DOM元素的id构造出一个编辑器
    editor = CodeMirror.fromTextArea(document.getElementById("policyDocument"), {
            mode:"application/json",
            lineNumbers: true,  //显示行号
            theme: "default",   //设置主题
            lineWrapping: false, //false则超过宽带会显示水平滚动条，true不会显示
            foldGutter: true,   //代码是否可折叠
            gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
            matchBrackets: true,    //括号匹配
            indentWithTabs: true,  //前 N*tabSize 个空格是否应替换为 N 个制表符
            smartIndent: true,   //上下文相关缩进（即是否缩进与之前的行相同）
            autofocus: true,
            styleActiveLine: true, //光标所在行高亮
            //readOnly: true,      //只读
    });

    getApplication();
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
  policyDocument = editor.getValue();
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
        editor.setValue(data);
//      document.getElementById("policyDocument").value = data;
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

function getApplication(){
  $.ajax({
    url: "../" + "common/getApplication.do",
    type: "POST",
    data: {},
    success: function (result) {
      if (result.success) {
        var application = document.getElementById("application");
        var data = result.data;
        for (var i = application.length - 1; i >= 1; i--) {
          application.remove(i);
        }
        for (var i = 0; i < data.length; i++) {
          application.options.add(new Option(data[i], data[i]));
        }
        application.value = "";
      } else {
        console.log("data.message: " + result.errorMsg);
        alert(result.errorMsg);
      }
    },
  })
}