package cc.landingzone.dreamweb.demo.ssologin;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.model.WebResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SSOLoginController extends BaseController {
    @RequestMapping("/listLoginUsers.do")
    public void listLoginUsers(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Map<String, Object> ids = new HashMap<>();
            ids.put("aliyunUserId", CommonConstants.Aliyun_UserId);
            ids.put("roleIds", CommonConstants.SSO_LOGIN_ROLE_IDS);
            ids.put("userIds", CommonConstants.SSO_LOGIN_USER_IDS);
            ids.put("cloudUserIds", CommonConstants.SSO_LOGIN_CLOUD_USER_IDS);
            result.setData(ids);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
