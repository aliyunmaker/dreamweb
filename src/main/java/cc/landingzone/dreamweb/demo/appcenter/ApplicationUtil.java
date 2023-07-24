package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.ApplicationEnum;
import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ResourceUtil;
import cc.landingzone.dreamweb.common.ServiceEnum;
import cc.landingzone.dreamweb.demo.appcenter.model.Application;
import cc.landingzone.dreamweb.demo.appcenter.model.Service;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
import com.aliyun.tag20180828.models.ListTagResourcesResponseBody;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cc.landingzone.dreamweb.common.ClientHelper.createTagClient;

/**
 * Utils for application center
 * @author 恬裕
 */
@Component
public class ApplicationUtil {
    /**
     * Get information of a specific application, including app name, app description and counts of
     * @param applicationEnum
     * @return
     * @throws Exception
     */
    public static Application getApplicationInfo(ApplicationEnum applicationEnum) throws Exception {
        Application application = new Application();
        application.setAppName(applicationEnum.getName());
        application.setDescription(applicationEnum.getDescription());
        application.setServicesCounts(listServicesCountsByApplication(applicationEnum.getName()));
        return application;
    }

    public static Map<String, Integer> listServicesCountsByApplication(String appName) throws Exception {
        Map<String, Integer> servicesCount = new HashMap<>(ServiceEnum.values().length);
        for (ServiceEnum serviceEnum: ServiceEnum.values()) {
            servicesCount.put(serviceEnum.name(), 0);
        }

        List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = ResourceUtil.getResourcesByTag(CommonConstants.APPLICATION_TAG_KEY, appName);

        for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resource: resourcesResponse) {
            String serviceName = resource.getResourceARN().split(":")[2].toUpperCase();
            if ("LOG".equals(serviceName)) {
                serviceName = "SLS";
            }
            String resourceType = resource.getResourceARN().split(":")[5].split("/")[0];
            try {
                ServiceEnum serviceEnum = ServiceEnum.valueOf(serviceName);
                // resourceType要为特定类型，如：ecs instance
                if (servicesCount.get(serviceName) != null && resourceType.equals(serviceEnum.getResourceType().split("::")[2].toLowerCase())) {
                    servicesCount.merge(serviceName, 1, Integer::sum);
                }
            } catch (Exception ignored) {

            }
        }

        return servicesCount;
    }

    public static Service getServiceByApplication(String appName, ServiceEnum serviceEnum) throws Exception {
        Service service = new Service();

        com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter tagFilter = new com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter()
                .setKey(CommonConstants.APPLICATION_TAG_KEY)
                .setValue(appName);
        String resourceType = serviceEnum.getResourceType();

        com.aliyun.tag20180828.Client client = createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

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
    }
}
