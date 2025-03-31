package cc.landingzone.dreamcmp.demo.ssologin;

import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.demo.sso.SSOConstants;

import cc.landingzone.dreamcmp.demo.sso.utils.AccessTokenUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SSOLoginController extends BaseController {
    @RequestMapping("/listLoginUsers.do")
    public void listLoginUsers(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            JSONObject ids = new JSONObject();
            ids.put("aliyunUserId", CommonConstants.Aliyun_UserId);
            ids.put("roles", SSOConstants.SSO_LOGIN_ROLE_IDS);
            ids.put("users", SSOConstants.SSO_LOGIN_USER_IDS);
            ids.put("cloudUsers", SSOConstants.SSO_LOGIN_CLOUD_USER_IDS);
            result.setData(ids);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }


    /**
     * 钉钉单点登录回调
     *
     * @param request
     * @param response
     */
    // 构建出来填写在钉钉中的URL:
    // https://login.dingtalk.com/oauth2/auth?
    // redirect_uri=https%3A%2F%2Fchengchao.name%2Fspringrun%2Fdingding%2Fsso_login_callback.htm%3Fsso%3Daliyun_role
    // &response_type=code
    // &client_id=dingldj1qprpp9dzuq03
    // &scope=openid
    // &state=state
    // &prompt=consent
    @RequestMapping("/dingding/sso_login_callback.do")
    public void dingding_sso_login_callback(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");
            String userAccessToken = AccessTokenUtil.getUserAccessToken(AccessTokenUtil.APP_DING, code);
            String redirectUrl = "dingtalk://dingtalkclient/page/link?url=?token=" + userAccessToken;

            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 飞书单点登录回调
     *
     * @param request
     * @param response
     */
    @RequestMapping("/feishu/sso_login_callback.do")
    public void feishu_sso_login_callback(HttpServletRequest request, HttpServletResponse response) {
        try {
            String code = request.getParameter("code");
            String userAccessToken = AccessTokenUtil.getUserAccessToken(AccessTokenUtil.APP_FEISHU, code);
            String redirectUrl = "https://console.aliyun.com/?access_token=" + userAccessToken;
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
