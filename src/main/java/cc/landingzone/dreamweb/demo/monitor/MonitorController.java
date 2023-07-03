package cc.landingzone.dreamweb.demo.monitor;

import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
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

@Controller
public class MonitorController extends BaseController {

    @RequestMapping("/signSLSMonitor.do")
    public void signSLSMonitor(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            long start = System.currentTimeMillis();
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String destination = request.getParameter("destination");
            String projectName = request.getParameter("projectName");
            String instanceId = request.getParameter("instanceId");

            if (StringUtils.isBlank(projectName)) {
                projectName = "application1-dreamweb";
            }

            if (StringUtils.isBlank(instanceId)) {
                instanceId = "application1-monitor";
            }

            if (StringUtils.isBlank(destination)) {
               destination = "https://sls4service.console.aliyun.com/lognext/app/monitor/" + projectName
                        + "/" + instanceId + "?resource=/fullmonitor/project/" + projectName +
                        "/logstore/logstore/dashboardtemplate/fullstack-monitor-host-common-detail" +
                        "&isShare=true&hideTopbar=true&hideSidebar=true&ignoreTabLocalStorage=true";
            }

            String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                    CommonConstants.Aliyun_AccessKeySecret,
                    CommonConstants.ADMIN_ROLE_ARN, username, true);
            String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
                    + URLEncoder.encode(destination, StandardCharsets.UTF_8.displayName())
                    + "&LoginUrl=https%3a%2f%2faliyun.com&SigninToken="
                    + signToken;
            response.sendRedirect(redirectUrl);
            logger.info("signSLSMonitor cost: " + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}