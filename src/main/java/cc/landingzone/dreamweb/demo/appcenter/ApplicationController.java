package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.ApplicationEnum;
import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceEnum;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.demo.appcenter.model.Application;
import cc.landingzone.dreamweb.demo.appcenter.model.Service;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/apps")
public class ApplicationController extends BaseController {

    @RequestMapping("/listApps.do")
    public void listApps(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<Application> applications = new ArrayList<>();
            for (ApplicationEnum applicationName: ApplicationEnum.values()) {
                Application application = getApplicationInfo(applicationName.name());
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

    public Application getApplicationInfo(String applicationName) {
        Application application = new Application();
        application.setAppName(applicationName);
        List<Service> serviceList = new ArrayList<>();

        com.aliyun.tag20180828.Client client = createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter tagFilter = new com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter()
                .setKey("Application")
                .setValue(applicationName);

        for (ServiceEnum serviceName: ServiceEnum.values()) {
            Service service = new Service();
            String resourceType = serviceName.getResourceType();

            try {
                assert client != null;
                com.aliyun.tag20180828.models.ListResourcesByTagRequest listResourcesByTagRequest = new com.aliyun.tag20180828.models.ListResourcesByTagRequest()
                        .setResourceType(resourceType)
                        .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                        .setTagFilter(tagFilter);
                com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
                com.aliyun.tag20180828.models.ListResourcesByTagResponse response = client.listResourcesByTagWithOptions(listResourcesByTagRequest, runtime);
                List<ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResources> resourcesResponse = response.getBody().getResources();
                service.setServiceName(serviceName.name());
                service.setResourceSize(resourcesResponse.size());
                serviceList.add(service);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        application.setServices(serviceList);
        return application;
    }

    public com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "tag.aliyuncs.com";
        try {
            return new com.aliyun.tag20180828.Client(config);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}