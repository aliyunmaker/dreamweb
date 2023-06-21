var aliyunUserId;
var roleIds;
var userIds;
var cloudUserIds;
$(document).ready(function() {
    $.ajax({
        url: "../listLoginUsers.do",
        success: function(result){
            if (result.success) {
                aliyunUserId = result.data.aliyunUserId;
                roleIds = result.data.roleIds;
                userIds = result.data.userIds;
                cloudUserIds = result.data.cloudUserIds;
                listLoginUsers();
            } else {
                alert(result.errorMsg);
            }
        }
    });
});

function listLoginUsers() {
    var aliyunLoginContainer = $("#aliyunLoginContainer");
    aliyunLoginContainer.empty();

    roleIds.forEach(function(roleId) {
        var item = `
        <div class="col d-flex align-items-start">
          <div
            class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-6 flex-shrink-0 me-3">
            <i class="iconfont icon-clothes" style="font-size: 1.8rem;"></i>
          </div>
          <div>
            <h3 class="fs-5">管理员</h3>
            <p>account: ${aliyunUserId}<br /> role: ${roleId}</p>
            <a target="_blank" href="../sso/login.do?sp=aliyun&userRoleId=${roleId}" class="btn btn-primary">
              <i class="iconfont icon-login" ></i> Console
            </a>
            <a target="_blank" href="../sso/downloadToken.do?sp=aliyun&userRoleId=${roleId}" class="btn btn-success">
              <i class="iconfont icon-download" ></i> STS Token
            </a>
          </div>
        </div>`;
        aliyunLoginContainer.append(item);
    });

    userIds.forEach(function(userId) {
        var item = `
        <div class="col d-flex align-items-start">
          <div
            class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-6 flex-shrink-0 me-3">
            <i class="iconfont icon-user-blank" style="font-size: 1.8rem;"></i>
          </div>
          <div>
            <h3 class="fs-5">云效账号</h3>
            <p>account: ${aliyunUserId}<br /> user: ${userId}</p>
            <a target="_blank" href="../sso/login.do?sp=aliyun_user&userRoleId=${userId}" class="btn btn-primary">
              <i class="iconfont icon-login" ></i> Console
            </a>
          </div>
        </div>`;
        aliyunLoginContainer.append(item);
    });

    cloudUserIds.forEach(function(cloudUserId) {
        var item = `
        <div class="col d-flex align-items-start">
          <div
            class="icon-square text-body-emphasis bg-body-secondary d-inline-flex align-items-center justify-content-center fs-6 flex-shrink-0 me-3">
            <i class="iconfont icon-user-blank" style="font-size: 1.8rem;"></i>
          </div>
          <div>
            <h3 class="fs-5">CloudSSO-管理员</h3>
            <p>account: ${aliyunUserId}<br /> cloud sso user: ${cloudUserId}</p>
            <a target="_blank" href="../sso/login.do?sp=aliyun_user_cloudsso&userRoleId=${cloudUserId}" class="btn btn-primary">
              <i class="iconfont icon-login" ></i> Console
            </a>
          </div>
        </div>`;
        aliyunLoginContainer.append(item);
    });
}