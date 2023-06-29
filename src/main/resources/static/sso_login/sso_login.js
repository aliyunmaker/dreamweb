var aliyunUserId;
var roles;
var users;
var cloudUsers;
$(document).ready(function() {
    $.ajax({
        url: "../listLoginUsers.do",
        success: function(result){
            if (result.success) {
                console.log(result.data);
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
          <a target="_blank" href="../sso/downloadToken.do?sp=${role.provider}&userRoleId=${role.id}" class="btn btn-success">
          <i class="iconfont icon-download" ></i> STS Token
          </a>`;
          $("#" + role.id + "-info").append(stsTokenBtn);
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
            <p>account: ${user.account}<br /> user: ${user.id}</p>
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