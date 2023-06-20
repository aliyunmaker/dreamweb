package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.ApplicationEnum;
import cc.landingzone.dreamweb.common.BaseController;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceEnum;
import cc.landingzone.dreamweb.common.model.WebResult;
import cc.landingzone.dreamweb.demo.appcenter.model.Application;
import cc.landingzone.dreamweb.demo.appcenter.model.Resource;
import cc.landingzone.dreamweb.demo.appcenter.model.Service;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
import com.aliyun.tag20180828.models.ListTagResourcesResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
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
                Application application = getApplicationsInfo(applicationEnum);
                assert application != null;
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

            List<Resource> resources = new ArrayList<>();

            for (ServiceEnum serviceEnum: ServiceEnum.values()) {
                Service service = getServiceByApplication(appName, serviceEnum);
                resources.addAll(getResourcesByService(service, appName));
            }

            result.setTotal(resources.size());
            result.setData(resources);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    public Application getApplicationsInfo(ApplicationEnum applicationEnum) {
        Application application = new Application();
        application.setAppName(applicationEnum.name());
        application.setDescription(applicationEnum.getDescription());
        application.setServicesCounts(listServicesByApplication(applicationEnum.name()));
        return application;
    }

    public Map<String, Integer> listServicesByApplication(String appName) {
        Map<String, Integer> servicesCount = new HashMap<>(ServiceEnum.values().length);
        for (ServiceEnum serviceEnum: ServiceEnum.values()) {
            servicesCount.put(serviceEnum.name(), 0);
        }

        com.aliyun.tag20180828.Client client = createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

        try {
            assert client != null;

            com.aliyun.tag20180828.models.ListTagResourcesRequest listTagResourcesRequest = new com.aliyun.tag20180828.models.ListTagResourcesRequest()
                    .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                    .setTags("{\"Application\":\""+ appName + "\"}")
                    .setPageSize(1000);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.tag20180828.models.ListTagResourcesResponse response = client.listTagResourcesWithOptions(listTagResourcesRequest, runtime);
            List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = response.getBody().getTagResources();

            for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resource: resourcesResponse) {
                String resourceType = resource.getResourceARN().split(":")[2].toUpperCase();
                servicesCount.merge(resourceType, 1, Integer::sum);
            }

            return servicesCount;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public Service getServiceByApplication(String appName, ServiceEnum serviceEnum) {
        Service service = new Service();

        com.aliyun.tag20180828.Client client = createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter tagFilter = new com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter()
                .setKey("Application")
                .setValue(appName);
        String resourceType = serviceEnum.getResourceType();

        try {
            assert client != null;

            com.aliyun.tag20180828.models.ListResourcesByTagRequest listResourcesByTagRequest = new com.aliyun.tag20180828.models.ListResourcesByTagRequest()
                    .setResourceType(resourceType)
                    .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                    .setTagFilter(tagFilter);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.tag20180828.models.ListResourcesByTagResponse response = client.listResourcesByTagWithOptions(listResourcesByTagRequest, runtime);
            List<ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResources> resourcesResponse = response.getBody().getResources();

            List<String> resourceIds = new ArrayList<>();
            for (ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResources resource: resourcesResponse) {
                resourceIds.add(resource.getResourceId());
            }

            service.setServiceName(serviceEnum.name());
            service.setResourceCount(resourcesResponse.size());
            service.setResourceIds(resourceIds);
            return service;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Resource> getResourcesByService(Service service, String appName) throws Exception {
        ServiceEnum serviceEnum = ServiceEnum.valueOf(service.getServiceName());
        return serviceEnum.getResources(service.getResourceIds());
    }

    public com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("tag.aliyuncs.com");
        try {
            return new com.aliyun.tag20180828.Client(config);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}