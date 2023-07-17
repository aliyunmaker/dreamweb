package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.*;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamweb.demo.appcenter.model.Event;
import cc.landingzone.dreamweb.demo.appcenter.model.Resource;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("resources/")
public class ResourceController extends BaseController {

    @RequestMapping("/signInConsole.do")
    public void signInConsole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String serviceName = request.getParameter("serviceName");
            String regionId = request.getParameter("regionId");
            String resourceId = request.getParameter("resourceId");
            String consoleUrl = null;
            String policy = null;

            switch (serviceName) {
                case "ECS":
                    policy = ServiceHelper.generatePolicyDocument("ecs", Collections.singletonList(resourceId), 3, CommonConstants.Aliyun_UserId);
                    consoleUrl = "https://ecs.console.aliyun.com/server/" + resourceId + "/detail?regionId=" + regionId;
                    break;
                case "OSS":
                    policy = ServiceHelper.generatePolicyDocument("oss", Collections.singletonList(resourceId), 3, CommonConstants.Aliyun_UserId);
                    consoleUrl = "https://oss.console.aliyun.com/bucket/oss-" + regionId + "/" + resourceId;
                    break;
                case "SLB":
                    policy = ServiceHelper.generatePolicyDocument("slb", Collections.singletonList(resourceId), 3, CommonConstants.Aliyun_UserId);
                    consoleUrl = "https://slbnew.console.aliyun.com/slb/" + regionId + "/slbs/" + resourceId;
                    break;
                case "RDS":
                    policy = ServiceHelper.generatePolicyDocument("rds", Collections.singletonList(resourceId), 3, CommonConstants.Aliyun_UserId);
                    consoleUrl = "https://rdsnext.console.aliyun.com/detail/" + resourceId + "/basicInfo";
                    break;
                case "SLS":
                    policy = ServiceHelper.generatePolicyDocument("log", Collections.singletonList(resourceId), 3, CommonConstants.Aliyun_UserId);
                    consoleUrl = "https://sls.console.aliyun.com/lognext/project/" + resourceId + "/overview";
                    break;
                default:
                    break;
            }

            assert policy != null;
            String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                    CommonConstants.Aliyun_AccessKeySecret,
                    CommonConstants.ADMIN_ROLE_ARN, username, policy, false);

            String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
                    + URLEncoder.encode(consoleUrl, StandardCharsets.UTF_8.displayName())
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

    @RequestMapping("/getResourceDetail.do")
    public void getResourceDetail(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String serviceName = request.getParameter("serviceName");
            String resourceId = request.getParameter("resourceId");

            Resource resource = new Resource();
            resource.setResourceId(resourceId);
            resource.setServiceName(serviceName);
            GetResourceHelper.getResourceDetail(resource);

            List<Event> events = ResourceUtil.listResourceEvents(resourceId);
            JSONObject resourceDetail = new JSONObject();
            resourceDetail.put("resource", resource);
            resourceDetail.put("events", events);

            result.setTotal(resourceDetail.size());
            result.setData(resourceDetail);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getSessionPolicy.do")
    public void getSessionPolicy(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String serviceName = request.getParameter("serviceName");
            String resourceId = request.getParameter("resourceId");

            if ("SLS".equals(serviceName)) {
                serviceName = "log";
            }

            String policy = ServiceHelper.generatePolicyDocument(serviceName.toLowerCase(), Collections.singletonList(resourceId), 3, CommonConstants.Aliyun_UserId);

            result.setData(policy);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
