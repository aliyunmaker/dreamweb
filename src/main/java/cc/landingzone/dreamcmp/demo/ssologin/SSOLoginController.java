package cc.landingzone.dreamcmp.demo.ssologin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.demo.sso.SSOConstants;

import org.apache.commons.lang3.StringUtils;
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


//    /**
//     * 钉钉单点登录回调
//     *
//     * @param request
//     * @param response
//     */
//    // 构建出来填写在钉钉中的URL:
//    // https://login.dingtalk.com/oauth2/auth?
//    // redirect_uri=https%3A%2F%2Fchengchao.name%2Fspringrun%2Fdingding%2Fsso_login_callback.htm%3Fsso%3Daliyun_role
//    // &response_type=code
//    // &client_id=dingldj1qprpp9dzuq03
//    // &scope=openid
//    // &state=state
//    // &prompt=consent
//    @RequestMapping("/dingding/sso_login_callback.do")
//    public void dingding_sso_login_callback(HttpServletRequest request, HttpServletResponse response) {
//        String result = "";
//        try {
//            String code = request.getParameter("code");
//            String sso = request.getParameter("sso");
//            String userAccessToken =
//                DingAccessTokenUtil.getUserAccessToken(DingAccessTokenUtil.SSO_DEMO_ID,
//                    DingAccessTokenUtil.SSO_DEMO_SECRET, code);
//            Map<String, String> resultMap = DingAccessTokenUtil.getUnionId(userAccessToken);
//            if ("demo".equals(sso)) {
//                User user = userService.getUserByLoginName("kidccc@gmail.com");
//                grantUserAccess(user, request, response, LoginMethod.DingDing, "do-not-redirect");
//                Map<String, Object> contextMap = new HashMap<String, Object>();
//                String avatarUrl = resultMap.get("avatarUrl");
//                if (StringUtils.isBlank(avatarUrl)) {
//                    avatarUrl = "https://chengchaosite.oss-cn-hangzhou.aliyuncs.com/img/sso_demo_avatar.png";
//                }
//                contextMap.put("avatarUrl", avatarUrl);
//                contextMap.put("nick", "钉钉: " + resultMap.get("nick"));
//                contextMap.put("ssoUrl", "../sso/login.htm?sp=aliyun_role_sso&userRoleId=47&sessionName=dingding_momo"
//                    + DateFormatter.toDateString(new Date(), "yyyyMMdd_HHmm"));
//                String responseStr = FreeMarkerUtils.buildFreemarkPage("enterprise-sso-demo.ftl", contextMap);
//                response.setContentType("text/html;charset=UTF-8");
//                response.getWriter().write(responseStr);
//                response.getWriter().flush();
//                return;
//            } else {
//                String dingdingUnionid = resultMap.get("unionId");
//                User user = userService.getUserByDingdingUnionid(dingdingUnionid);
//                grantUserAccess(user, request, response, LoginMethod.DingDing, null);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            result = e.getMessage();
//        }
//        outputToString(response, result);
//    }
}
