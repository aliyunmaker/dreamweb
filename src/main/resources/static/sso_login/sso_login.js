var aliyunUserId;
var roles;
var users;
var cloudUsers;
var tokenText;

$(document).ready(function() {
    $.ajax({
        url: "../listLoginUsers.do",
        success: function(result){
            if (result.success) {
                aliyunUserId = result.data.aliyunUserId;
                roles = result.data.roles;
                users = result.data.users;
                cloudUsers = result.data.cloudUsers;
                listLoginUsers();
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function listLoginUsers() {
    $("#aliyunLoginContainer").empty();
    $("#awsLoginContainer").empty();
    $("#tencentLoginContainer").empty();

    roles.forEach(function(role) {
        var item = `
        <div class="col d-flex align-items-start">
          <div
            class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-6 flex-shrink-0 me-3">
            <i class="iconfont icon-clothes" style="font-size: 1.8rem;"></i>
          </div>
          <div id="${role.id}-info">
            <h3 class="fs-5">${role.name}</h3>
            <p>account: ${role.account}<br /> role: ${role.id}</p>
            <a target="_blank" href="../sso/login.do?sp=${role.provider}&userRoleId=${role.id}" class="btn btn-primary">
              <i class="iconfont icon-login" ></i> Console
            </a>
          </div>
        </div>`;
        $("#" + role.provider + "LoginContainer").append(item);
        if (role.provider === "aliyun") {
          var stsTokenBtn = `
          <a onclick="getSTSToken('${role.id}')" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#STSTokenModal-${role.id}">
          <i class="iconfont icon-download" ></i> STS Token
          </a>`;
          $("#" + role.id + "-info").append(stsTokenBtn);
          var tokenModal = `
          <div class="modal fade" id="STSTokenModal-${role.id}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
              <div class="modal-content">
                <div class="modal-header">
                  <h1 class="modal-title fs-5" id="exampleModalLabel">STS Token</h1>
                  <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="modelBody-${role.id}" style="word-wrap: break-word;">
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                  <button type="button" class="btn btn-primary" onclick="copyToken('${role.id}')">Copy token</button>
                </div>
              </div>
            </div>
          </div>`;
          $("#mainContainer").append(tokenModal);
        }
    });

    users.forEach(function(user) {
        var item = `
        <div class="col d-flex align-items-start">
          <div
            class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-6 flex-shrink-0 me-3">
            <i class="iconfont icon-user-blank" style="font-size: 1.8rem;"></i>
          </div>
          <div>
            <h3 class="fs-5">${user.name}</h3>
            <p>account: ${user.account}<br /> user: ${user.id}
            ${user.url !== "" ? `<br /> orgnization: ${user.url.split(" ")[0]}<br /> url: ${user.url.split(" ")[1]}` : ""}</p>
            <a target="_blank" href="../sso/login.do?sp=${user.provider}_user&userRoleId=${user.id}" class="btn btn-primary">
              <i class="iconfont icon-login" ></i> Console
            </a>
          </div>
        </div>`;
        $("#" + user.provider + "LoginContainer").append(item);
    });

    cloudUsers.forEach(function(cloudUser) {
        var item = `
        <div class="col d-flex align-items-start">
          <div
            class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-6 flex-shrink-0 me-3">
            <i class="iconfont icon-user-blank" style="font-size: 1.8rem;"></i>
          </div>
          <div>
            <h3 class="fs-5">${cloudUser.name}</h3>
            <p>account: ${cloudUser.account}<br /> cloud sso user: ${cloudUser.id}</p>
            <a target="_blank" href="../sso/login.do?sp=${cloudUser.provider}_user_cloudsso&userRoleId=${cloudUser.id}" class="btn btn-primary">
              <i class="iconfont icon-login" ></i> Console
            </a>
          </div>
        </div>`;
        $("#" + cloudUser.provider + "LoginContainer").append(item);
    });
}

function getSTSToken(roleId) {
  $.ajax({
    url: "../sso/downloadToken.do?sp=aliyun&userRoleId=" + roleId,
    success: function(result){
        if (result) {
            var tokenContent = JSON.parse(result.data);
            showSTSToken(roleId, tokenContent);
        } else {
            alert(result.errorMsg);
        }
    }
  });
}

function showSTSToken(roleId, tokenContent) {
  // var modalBody = document.getElementById("modelBody-" + roleId);
  var modalBody = $("#modelBody-" + roleId);
  // var token = "AccessKeyId: " + tokenContent.Credentials.AccessKeyId
  // + "\r\nAccessKeySecret: " + tokenContent.Credentials.AccessKeySecret
  // + "\r\nSecurityToken: " + tokenContent.Credentials.SecurityToken;
  modalBody.empty();
  var token = `<div class="mb-3">
  <label for="AccessKeyIdInput" class="form-label"><b>AccessKeyId</b></label>
  <input class="form-control" id="AccessKeyIdInput" type="text" value="${tokenContent.Credentials.AccessKeyId}" aria-label="readonly input example" readonly>
  </div>
  <div class="mb-3">
  <label for="AccessKeySecretInput" class="form-label"><b>AccessKeySecret</b></label>
  <input class="form-control" id="AccessKeySecretInput" type="text" value="${tokenContent.Credentials.AccessKeySecret}" aria-label="readonly input example" readonly>
  </div>
  <div class="mb-3">
  <label for="SecurityTokenTextarea" class="form-label"><b>SecurityToken</b></label>
  <textarea class="form-control" id="SecurityTokenTextarea" rows="8" readonly>${tokenContent.Credentials.SecurityToken}</textarea>
  </div>
  `;
  tokenText = "AccessKeyId: \n" + tokenContent.Credentials.AccessKeyId + "\n" +
  "AccessKeySecret: \n" + tokenContent.Credentials.AccessKeySecret + "\n" +
  "SecurityToken: \n" + tokenContent.Credentials.SecurityToken;
  modalBody.append(token);
  $("#STSTokenModal-" + roleId).modal('show');
}

async function copyToken() {
  try {
    await navigator.clipboard.writeText(tokenText);
    // document.getElementById("modelBody-" + roleId).append('\nContent copied to clipboard');
    // alert('Content copied to clipboard');
  } catch (err) {
    // document.getElementById("modelBody-" + roleId).append('\nFailed to copy: ', err);
    // alert('Failed to copy: ', err);
  }
}