package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.ApplicationEnum;
import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.GetResourceHelper;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamweb.demo.appcenter.model.Application;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/apps")
public class ApplicationController extends BaseController {

    @RequestMapping("/listApps.do")
    public void listApps(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<Application> applications = new ArrayList<>();
            for (ApplicationEnum applicationEnum: ApplicationEnum.values()) {
                Application application = ApplicationUtil.getApplicationInfo(applicationEnum);
                applications.add(application);
            }
            result.setTotal(applications.size());
            result.setData(applications);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getAppDetail.do")
    public void getAppDetail(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String appName = request.getParameter("appName");

            List<Resource> resources = ApplicationUtil.listResourcesByApplication(appName);

            // 获取环境类型
            ApplicationUtil.setEnvironmentType(resources);

            Map<String, List<Resource>> servicesResources = ApplicationUtil.getResourcesByServices(resources);

            // 设置操作，设置resourceType
            ApplicationUtil.setOperations(servicesResources);

//            for (ServiceEnum serviceEnum: ServiceEnum.values()) {
//                Service service = ApplicationUtil.getServiceByApplication(appName, serviceEnum);
//                resources.addAll(ApplicationUtil.getResourcesByService(service, appName));
//            }

            result.setTotal(servicesResources.size());
            result.setData(servicesResources);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/signInConsole.do")
    public void signInConsole(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String serviceName = request.getParameter("serviceName");
            String regionId = request.getParameter("regionId");
            String resourceId = request.getParameter("resourceId");
            String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                    CommonConstants.Aliyun_AccessKeySecret,
                    CommonConstants.ADMIN_ROLE_ARN, username, false);
            String consoleUrl = null;

            if ("ECS".equals(serviceName)) {
                consoleUrl = "https://ecs.console.aliyun.com/server/" + resourceId + "/detail?regionId=" + regionId;
            } else if ("OSS".equals(serviceName)) {
                consoleUrl = "https://oss.console.aliyun.com/bucket/oss-" + regionId + "/" + resourceId;
            } else if ("SLB".equals(serviceName)) {
                consoleUrl = "https://slbnew.console.aliyun.com/slb/" + regionId + "/slbs/" + resourceId;
            } else if ("RDS".equals(serviceName)) {
                consoleUrl = "https://rdsnext.console.aliyun.com/detail/" + resourceId + "/basicInfo";
            } else if ("SLS".equals(serviceName)) {
                consoleUrl = "https://sls.console.aliyun.com/lognext/project/" + resourceId + "/overview";
            }

            assert consoleUrl != null;
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

            List<Event> events = ApplicationUtil.listResourceEvents(resourceId);
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
}