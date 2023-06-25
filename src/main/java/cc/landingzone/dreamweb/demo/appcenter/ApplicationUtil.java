package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.*;
import cc.landingzone.dreamweb.demo.appcenter.model.Application;
import cc.landingzone.dreamweb.demo.appcenter.model.Event;
import cc.landingzone.dreamweb.demo.appcenter.model.Resource;
import cc.landingzone.dreamweb.demo.appcenter.model.Service;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
import com.aliyun.tag20180828.models.ListTagResourcesResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

import static cc.landingzone.dreamweb.common.ServiceHelper.createTagClient;

@Component
public class ApplicationUtil {
    public static Application getApplicationInfo(ApplicationEnum applicationEnum) throws Exception {
        Application application = new Application();
        application.setAppName(applicationEnum.name());
        application.setDescription(applicationEnum.getDescription());
        application.setServicesCounts(listServicesCountsByApplication(applicationEnum.name()));
        return application;
    }

    public static Map<String, Integer> listServicesCountsByApplication(String appName) throws Exception {
        Map<String, Integer> servicesCount = new HashMap<>(ServiceEnum.values().length);
        for (ServiceEnum serviceEnum: ServiceEnum.values()) {
            servicesCount.put(serviceEnum.name(), 0);
        }

        List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = getResourcesByTag(CommonConstants.APPLICATION_TAG_KEY, appName);

        for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resource: resourcesResponse) {
            String serviceName = resource.getResourceARN().split(":")[2].toUpperCase();
            if ("LOG".equals(serviceName)) {
                serviceName = "SLS";
            }
            servicesCount.merge(serviceName, 1, Integer::sum);
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

//    public static List<Resource> getResourcesByService(Service service, String appName) throws Exception {
//        ServiceEnum serviceEnum = ServiceEnum.valueOf(service.getServiceName());
//        return GetResourceHelper.getResources(service.getResourceIds(), serviceEnum);
//    }

    private static List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> getResourcesByTag(String tagKey, String tagValue) throws Exception {
        com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

        com.aliyun.tag20180828.models.ListTagResourcesRequest listTagResourcesRequest = new com.aliyun.tag20180828.models.ListTagResourcesRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTags("{\""+ tagKey + "\":\""+ tagValue + "\"}")
                .setPageSize(1000);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.tag20180828.models.ListTagResourcesResponse response = client.listTagResourcesWithOptions(listTagResourcesRequest, runtime);
        return response.getBody().getTagResources();
    }

    private static List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> getTagResourcesByApplication(String appName) throws Exception {
        com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

        com.aliyun.tag20180828.models.ListTagResourcesRequest listTagResourcesRequest = new com.aliyun.tag20180828.models.ListTagResourcesRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTags("{\"" + CommonConstants.APPLICATION_TAG_KEY + "\":\""+ appName + "\"}")
                .setPageSize(1000);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.tag20180828.models.ListTagResourcesResponse response = client.listTagResourcesWithOptions(listTagResourcesRequest, runtime);
        return response.getBody().getTagResources();
    }

    public static List<Resource> listResourcesByApplication(String appName) throws Exception {
        List<Resource> resources = new ArrayList<>();

        List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = getResourcesByTag(CommonConstants.APPLICATION_TAG_KEY, appName);

        for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resourceResponse: resourcesResponse) {
            // ARN format: arn:acs:${Service}:${Region}:${Account}:${ResourceType}/${ResourceId}
            String[] splitArn = resourceResponse.getResourceARN().split(":");
            String serviceName = splitArn[2].toUpperCase();
            if ("LOG".equals(serviceName)) {
                serviceName = "SLS";
            }
            String regionId = splitArn[3];
            String resourceId = splitArn[5].split("/")[1];

            Resource resource = new Resource();
            resource.setServiceName(serviceName);
            resource.setRegionId(regionId);
            resource.setResourceId(resourceId);
            resources.add(resource);
        }

        return resources;
    }

    public static Map<String, List<Resource>> getResourcesByServices(List<Resource> resources) {
        Map<String, List<Resource>> servicesResources = new HashMap<>(ServiceEnum.values().length);

        for (ServiceEnum serviceEnum: ServiceEnum.values()) {
            servicesResources.put(serviceEnum.name(), new ArrayList<>());
        }

        for (Resource resource: resources) {
            String serviceName = resource.getServiceName();
//            GetResourceHelper.getResourceDetail(resource); // 根据resourceId把resource detail查出来，一次查一个
            List<Resource> resourceList = servicesResources.get(serviceName);
            resourceList.add(resource);
            servicesResources.put(serviceName, resourceList);
        }

//        for (String serviceName: servicesResources.keySet()) {
//            GetResourceHelper.setResourcesDetails(appName, serviceName, servicesResources.get(serviceName)); // 根据serviceName把resource details查出来，一次查多个
//        }

        return servicesResources;
    }

    public static void setEnvironmentType(List<Resource> resources) throws Exception {
        Map<String, String> resourceEnvType = new HashMap<>();

        for (String environmentType: CommonConstants.ENVIRONMENT_TYPE_TAG_VALUES) {
            List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = getResourcesByTag(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY, environmentType);
            for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resourceResponse: resourcesResponse) {
                String[] splitArn = resourceResponse.getResourceARN().split(":");
                String resourceId = splitArn[5].split("/")[1];
                resourceEnvType.put(resourceId, environmentType);
            }
        }

        for (Resource resource: resources) {
            String envType = resourceEnvType.get(resource.getResourceId());
            if (StringUtils.isNotBlank(envType)) {
                resource.setEnvironmentType(envType);
            }
        }
    }

    public static void setOperations(Map<String, List<Resource>> servicesResources) {
        for (String serviceName: servicesResources.keySet()) {
            ServiceEnum serviceEnum = ServiceEnum.valueOf(serviceName);
            for (Resource resource: servicesResources.get(serviceName)) {
                resource.setResourceType(serviceEnum.getResourceType());
                Map<String, String> operations = new HashMap<>();
                operations.put("operationName", "");
                operations.put("operationUrl", "");
                if ("ECS".equals(serviceName)) {
                    String consoleUrl = "https://"+resource.getServiceName().toLowerCase()+".console.aliyun.com/server/region"+"/"+resource.getServiceName().toLowerCase()+"-"+resource.getRegionId()+"/"+resource.getResourceId();
                    String terminalUrl = "https://ecs-workbench.aliyun.com/?instanceType="+resource.getServiceName().toLowerCase()+"&regionId="+resource.getRegionId()+"&instanceId="+resource.getResourceId();
                    operations.put("consoleUrl", consoleUrl);
                    operations.put("operationName", "Terminal");
                    operations.put("operationUrl", terminalUrl);
                } else if ("OSS".equals(serviceName)) {
                    //试过了
                    String consoleUrl = "https://"+resource.getServiceName().toLowerCase()+".console.aliyun.com/"+resource.getResourceType().split("::")[2].toLowerCase()+"/"+resource.getServiceName().toLowerCase()+"-"+resource.getRegionId()+"/"+resource.getResourceId();
                    operations.put("consoleUrl", consoleUrl);
                } else if ("SLB".equals(serviceName)) {
                    String consoleUrl = "https://"+resource.getServiceName().toLowerCase()+".console.aliyun.com/"+resource.getResourceType().split("::")[2]+"/"+resource.getServiceName().toLowerCase()+"-"+resource.getRegionId()+"/"+resource.getResourceId();
                    operations.put("consoleUrl", consoleUrl);
                } else if ("RDS".equals(serviceName)) {
                    String consoleUrl = "https://"+resource.getServiceName().toLowerCase()+".console.aliyun.com/"+resource.getResourceType().split("::")[2]+"/"+resource.getServiceName().toLowerCase()+"-"+resource.getRegionId()+"/"+resource.getResourceId();
                    String sqlConsoleUrl = "https://dms.aliyun.com/?regionId="+resource.getRegionId()+"&dbType=mysql&instanceId="+resource.getResourceId()+"&instanceSource="+resource.getServiceName();
                    operations.put("consoleUrl", consoleUrl);
                    operations.put("operationName", "SQL Console");
                    operations.put("operationUrl", sqlConsoleUrl);
                } else {
                    //试过了
                    String consoleUrl = "https://"+resource.getServiceName().toLowerCase()+".console.aliyun.com/lognext/"+resource.getResourceType().split("::")[2].toLowerCase()+"/"+resource.getResourceId()+"/overview";
                    operations.put("consoleUrl", consoleUrl);
                }
                resource.setOperations(operations);
            }
        }
    }

    public static List<Event> listResourceEvents(String resourceId, Integer durationDays) throws Exception {
        Date now = new Date();


        List<Event> events = new ArrayList<>();

        com.aliyun.actiontrail20200706.Client client = ServiceHelper.createTrailClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.actiontrail20200706.models.LookupEventsRequest.LookupEventsRequestLookupAttribute lookupAttribute0 = new com.aliyun.actiontrail20200706.models.LookupEventsRequest.LookupEventsRequestLookupAttribute()
                .setKey("ResourceName")
                .setValue(resourceId);
        com.aliyun.actiontrail20200706.models.LookupEventsRequest lookupEventsRequest = new com.aliyun.actiontrail20200706.models.LookupEventsRequest()
                .setLookupAttribute(Collections.singletonList(lookupAttribute0));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();


        List<Map<String,?>> detailedEvents = client.lookupEventsWithOptions(lookupEventsRequest, runtime).getBody().getEvents();
        for (Map<String,?> detailedEvent: detailedEvents) {
            Event event = new Event();
            event.setEventTime(detailedEvent.get("eventTime").toString());
            event.setEventName(detailedEvent.get("eventName").toString());
            event.setUserIdentity(detailedEvent.get("userIdentity"));
            events.add(event);
        }

        return events;
    }
}
