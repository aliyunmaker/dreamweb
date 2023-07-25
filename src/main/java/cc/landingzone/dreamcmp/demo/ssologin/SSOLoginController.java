package cc.landingzone.dreamcmp.demo.ssologin;

import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.demo.sso.SSOConstants;

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
}
