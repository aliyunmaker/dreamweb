var appName;
var serviceName;
var appInfo;
var appDetail;
var sortedServiceNames = ["ECS", "RDS", "SLB", "OSS", "SLS"];
var editor = {};

$(document).ready(function() {
    var queryString = window.location.search;
    var urlParams = new URLSearchParams(queryString);
    appName = urlParams.get('appName');
    serviceName = urlParams.get('serviceName');

    var appDetailPage = `
    <div class="row pb-2 d-flex">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a href="#" onclick="showAppCenterPage()">Application center</a></li>
                <li class="breadcrumb-item" aria-current="page">Application detail</li>
            </ol>
        </nav>
        <h5 class="pb-3 fs-2">Resources - ${appName}</h5>
        <div class="row pb-2" id="appDetailBody">
        </div>
    </div>`;
    $("#appCenterPage").append(appDetailPage);

    $.ajax({
        url: "../apps/listApps.do",
        success: function(result){
            if (result.success) {
                appsInfo = result.data;
                $("#appDetailBody").empty();
                var tab = `<ul class="nav nav-tabs" id="tabs"></ul>`;
                $("#appDetailBody").append(tab);
                appsInfo.forEach(function (app) {
                    if (app.appName === appName) {
                        var sumCount = 0;
                        for (var serviceName in app.servicesCounts) {
                            sumCount += app.servicesCounts[serviceName];
                        };
                        $("#tabs").append(`<li class="nav-item">
                            <a class="nav-link text-black" aria-current="page" id="tab-all" onclick="listResourcesDetails('all')">All(${sumCount})</a>
                            </li>`);
                        for (var serviceName of sortedServiceNames) {
                            var tab = `
                            <li class="nav-item">
                                <a class="nav-link text-black" aria-current="page" id="tab-${serviceName}"
                                    onclick="listResourcesDetails('${serviceName}')">${serviceName}(${app.servicesCounts[serviceName]})</a>
                            </li>`;
                            $("#tabs").append(tab);
                        };
                    }
                });
                var table = `
                <table class="table">
                    <thead>
                    <tr>
                        <th scope="col">Resource ID/name</th>
                        <th scope="col">Resource Type</th>
                        <th scope="col">Environment Type</th>
                        <th scope="col">Region</th>
                        <th scope="col">Operations</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
                `;
                $("#appDetailBody").append(table);
                $("#appDetailBody tbody").append(
                `<div class="position-absolute top-50 start-50 translate-middle">
                    <div class="spinner-border" style="width: 5rem; height: 5rem;" role="status">
                    <span class="visually-hidden">Loading...</span>
                    </div>
                </div>`
                );
            } else {
                alert(result.errorMsg);
            }
        }
    });
    
    $.ajax({
        url: "../apps/getAppDetail.do?appName="+appName,
        success: function(result){
            if (result.success) {
                appDetail = result.data;
                listResourcesDetails(serviceName);
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function listResourcesDetails(queryServiceName) {
    var tabs = document.querySelectorAll("#tabs a");
    tabs.forEach(function(tab) {
        tab.classList.remove("active");
    });
    document.querySelector("#tab-"+queryServiceName).classList.add("active");
    $("#appDetailBody tbody").empty();
    
    for (var serviceName of sortedServiceNames) {
        editor[serviceName] = {};
        if (queryServiceName === "all" || serviceName === queryServiceName) {
            appDetail[serviceName].forEach(function(resource) {
                var row = `
                <tr>
                    <td><a href=# class="text-decoration-none" onclick="getResourceDetail('${serviceName}', '${resource.resourceId}')">
                    ${serviceName === "ECS"? resource.resourceId + " | " + resource.resourceName : resource.resourceId}</a></td>
                    <td>${resource.serviceName}</td>
                    <td>${resource.environmentType}</td>
                    <td>${resource.regionId}</td>
                    <td>
                        <a class="iconfont icon-policy text-black text-decoration-none"
                        data-bs-toggle="tooltip" data-bs-title="查看session policy" href="#" 
                        onclick="getSessionPolicy('${serviceName}', '${resource.resourceId}')"></a>
                        ${serviceName === "ECS" ? `
                        <div class="vr"></div>
                        <a class="iconfont icon-delete text-black text-decoration-none" 
                        data-bs-toggle="tooltip" data-bs-title="删除实例" href="#" 
                        onclick="openDeleteInstanceModal('${serviceName}', '${resource.resourceId}')"></a>` : ""}
                        <div class="vr"></div>
                        <a target="_blank" class="text-decoration-none" href="../resources/signInConsole.do?serviceName=${serviceName}&regionId=${resource.regionId}&resourceId=${resource.resourceId}">Console</a>
                        ${resource.operations["operationName"] !== "" ? `<div class="vr"></div>` : ""}
                        <a target="_blank" class="text-decoration-none" href="${resource.operations["operationUrl"]}">${resource.operations["operationName"]}</a>
                    </td>
                </tr>`;

                $("#appDetailBody tbody").append(row);

                var policyModal = `
                <div class="modal fade" id="sessionPolicyModal-${serviceName}-${resource.resourceId}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                        <h1 class="modal-title fs-5" id="exampleModalLabel">Session Policy</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body" id="modelBody-${serviceName}-${resource.resourceId}" style="word-wrap: break-word;">
                            <textarea class="form-control ms-2" id="policyDocument-${serviceName}-${resource.resourceId}" rows="10"></textarea>
                        </div>
                        <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary" onclick="copyPolicy('${serviceName}', '${resource.resourceId}')">Copy policy</button>
                        </div>
                    </div>
                    </div>
                </div>`;
                $("#appDetailBody").append(policyModal);

                var deleteInstanceModal = `
                <div class="modal fade" tabindex="-1" id="deleteInstanceModal-${serviceName}-${resource.resourceId}">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">删除实例</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <p>请再次确认是否要删除该实例？</p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-danger" onclick="deleteInstance('${serviceName}', '${resource.resourceId}')">Confirm</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>`;
                $("#appDetailBody").append(deleteInstanceModal);

                //根据DOM元素的id构造出一个编辑器
                editor[serviceName][resource.resourceId] = CodeMirror.fromTextArea(document.getElementById("policyDocument-"+serviceName+"-"+resource.resourceId), {
                    mode:"application/json",
                    lineNumbers: false,  //显示行号
                    theme: "default",   //设置主题
                    lineWrapping: false, //false则超过宽带会显示水平滚动条，true不会显示
                    foldGutter: true,   //代码是否可折叠
                    gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
                    matchBrackets: true,    //括号匹配
                    indentWithTabs: true,  //前 N*tabSize 个空格是否应替换为 N 个制表符
                    smartIndent: true,   //上下文相关缩进（即是否缩进与之前的行相同）
                    autofocus: true,
                    styleActiveLine: true, //光标所在行高亮
                    readOnly: true,      //只读
                    autoRefresh: true,   //如果为false，在modal中需要点一下才能显示
                });
            })
        }
    }
    $(function() { $("[data-bs-toggle='tooltip']").tooltip();});

    var deleteSucceedModal = `
    <div class="modal fade" tabindex="-1" id="deleteSucceedModal">
        <div class="modal-dialog">
            <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">删除成功</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" onclick=location.reload();></button>
            </div>
            <div class="modal-body">
                
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" onclick=location.reload();>Close</button>
            </div>
            </div>
        </div>
    </div>`;
    $("#appDetailBody").append(deleteSucceedModal);
}

// 资源详情
function getResourceDetail(serviceName, resourceId) {
    var page = "application_center/resource_detail.html?appName=" + appName + "&serviceName=" + serviceName + "&resourceId=" + resourceId;
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}

// 查看session policy
function getSessionPolicy(serviceName, resourceId) {
    $.ajax({
        url: "../resources/getSessionPolicy.do?serviceName="+serviceName+"&resourceId="+resourceId,
        success: function(result){
            if (result.success) {
                var sessionPolicy = result.data;
                console.log(sessionPolicy);
                showSessionPolicy(serviceName, resourceId, sessionPolicy);
            } else {
                alert(result.errorMsg);
            }
        }
    });
}

// 展示policy内容
function showSessionPolicy(serviceName, resourceId, sessionPolicy) {
    $("#sessionPolicyModal-" + serviceName + "-" + resourceId).modal('show');
    editor[serviceName][resourceId].setSize(null, 400);
    editor[serviceName][resourceId].setValue(sessionPolicy);
}

// 复制policy
async function copyPolicy(serviceName, resourceId) {
    try {
      // var str = document.getElementById("modelBody-" + resourceId).innerText;
      var str = editor[serviceName][resourceId].getValue();
      await navigator.clipboard.writeText(str);
      // document.getElementById("modelBody-" + roleId).append('\nContent copied to clipboard');
      // alert('Content copied to clipboard');
    } catch (err) {
      // document.getElementById("modelBody-" + roleId).append('\nFailed to copy: ', err);
      // alert('Failed to copy: ', err);
    }
}

// 返回应用中心
function showAppCenterPage() {
    var page = "application_center/application_center.html";
    var iframe = parent.document.getElementById("iframe");
    iframe.setAttribute("src", page);
}

// 确认是否删除ECS实例
function openDeleteInstanceModal(serviceName, resourceId) {
    $("#deleteInstanceModal-"+serviceName+"-"+resourceId).modal('show');
}

// 删除ECS实例
function deleteInstance(serviceName, resourceId) {
    $("#deleteInstanceModal-"+serviceName+"-"+resourceId).modal('hide');
    $.ajax({
        url: "../resources/deleteInstance.do?serviceName="+serviceName+"&resourceId="+resourceId,
        success: function(result){
            if (result.success) {
                var requestId = result.data;
                $("#deleteSucceedModal .modal-body").html(`<p>删除成功，requerstId: ${requestId}</p>`);
                $("#deleteSucceedModal").modal('show');
            } else {
                alert(result.errorMsg);
            }
        }
    });
}

// 获得参考说明内容
function getDocumentByModule(){
    var params = {
      module: "myapplication"
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