package cc.landingzone.dreamweb.demo.logcenter;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ResourceUtil;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.common.utils.AliyunAPIUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Controller
public class LogCenterController extends BaseController {

    @RequestMapping("/signSLS.do")
    public void signSLS(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String destination = request.getParameter("destination");
            String appName = request.getParameter("appName");
            String logStore = request.getParameter("logStore");
            String queryString = CommonConstants.QUERY_STRING;

            // 默认查看access log
            if (StringUtils.isBlank(logStore)) {
                logStore = "access-log";
            }

            String projectName = ResourceUtil.getSLSProjectNameByAppName(appName);

            if (StringUtils.isBlank(destination)) {
                destination = "https://sls4service.console.aliyun.com/lognext/project/" + projectName + "/logsearch/" + logStore + "?hideTopbar=true&hideSidebar=true&ignoreTabLocalStorage=true";
                if ("access-log".equals(logStore)) {
                    destination += "&queryString=" + queryString;
                }
            }

            String policy = ServiceHelper.generatePolicyDocument("log", Collections.singletonList(projectName), 3, CommonConstants.Aliyun_UserId);
            String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                    CommonConstants.Aliyun_AccessKeySecret,
                    CommonConstants.ADMIN_ROLE_ARN, username, policy, true);
            String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
                    + URLEncoder.encode(destination, StandardCharsets.UTF_8.displayName())
                    + "&LoginUrl=https%3a%2f%2faliyun.com&SigninToken="
                    + signToken;
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}