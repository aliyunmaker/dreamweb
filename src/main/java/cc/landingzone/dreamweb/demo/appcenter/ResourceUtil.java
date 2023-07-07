package cc.landingzone.dreamweb.demo.appcenter;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceEnum;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamweb.demo.appcenter.model.Event;
import cc.landingzone.dreamweb.demo.appcenter.model.Resource;
import com.aliyun.tag20180828.models.ListTagResourcesResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ResourceUtil {
    //    public static List<Resource> getResourcesByService(Service service, String appName) throws Exception {
//        ServiceEnum serviceEnum = ServiceEnum.valueOf(service.getServiceName());
//        return GetResourceHelper.getResources(service.getResourceIds(), serviceEnum);
//    }

    public static List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> getResourcesByTag(String tagKey, String tagValue) throws Exception {
        com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

        com.aliyun.tag20180828.models.ListTagResourcesRequest listTagResourcesRequest = new com.aliyun.tag20180828.models.ListTagResourcesRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTags("{\""+ tagKey + "\":\""+ tagValue + "\"}")
                .setPageSize(1000);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.tag20180828.models.ListTagResourcesResponse response = client.listTagResourcesWithOptions(listTagResourcesRequest, runtime);
        return response.getBody().getTagResources();
    }

//    private static List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> getTagResourcesByApplication(String appName) throws Exception {
//        com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
//
//        com.aliyun.tag20180828.models.ListTagResourcesRequest listTagResourcesRequest = new com.aliyun.tag20180828.models.ListTagResourcesRequest()
//                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
//                .setTags("{\"" + CommonConstants.APPLICATION_TAG_KEY + "\":\""+ appName + "\"}")
//                .setPageSize(1000);
//        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//        com.aliyun.tag20180828.models.ListTagResourcesResponse response = client.listTagResourcesWithOptions(listTagResourcesRequest, runtime);
//        return response.getBody().getTagResources();
//    }

    public static List<Resource> listResourcesByApplication(String appName) throws Exception {
        List<Resource> resources = new ArrayList<>();

        List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = getResourcesByTag(CommonConstants.APPLICATION_TAG_KEY, appName);

        for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resourceResponse: resourcesResponse) {
            // ARN format: arn:acs:${Service}:${Region}:${Account}:${ResourceType(e.g.instance)}/${ResourceId}
            String[] splitArn = resourceResponse.getResourceARN().split(":");
            String serviceName = splitArn[2].toUpperCase();
            if ("LOG".equals(serviceName)) {
                serviceName = "SLS";
            }
            String regionId = splitArn[3];
            String resourceType = splitArn[5].split("/")[0];
            String resourceId = splitArn[5].split("/")[1];

            try {
                ServiceEnum serviceEnum = ServiceEnum.valueOf(serviceName);
                // resourceType要为特定类型，如：ecs instance
                if (resourceType.equals(serviceEnum.getResourceType().split("::")[2].toLowerCase())) {
                    Resource resource = new Resource();
                    resource.setServiceName(serviceName);
                    resource.setRegionId(regionId);
                    resource.setResourceId(resourceId);
                    resources.add(resource);
                }
            } catch (Exception ignored) {
                // 如果serviceName不在ServiceEnum里面，跳过这个resource
            }
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
            List<Resource> resourceList = servicesResources.get(serviceName);
            if (resourceList != null) {
                resourceList.add(resource);
                servicesResources.put(serviceName, resourceList);
            }
        }

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

    /**
     * 获得ECS和RDS的特殊操作url
     * @param servicesResources
     */
    public static void setOperations(Map<String, List<Resource>> servicesResources) throws Exception {
        for (String serviceName: servicesResources.keySet()) {
            ServiceEnum serviceEnum = ServiceEnum.valueOf(serviceName);
            for (Resource resource: servicesResources.get(serviceName)) {
                resource.setResourceType(serviceEnum.getResourceType());
                Map<String, String> operations = new HashMap<>();
                if (!"ECS".equals(serviceName) && !"RDS".equals(serviceName)) {
                    operations.put("operationName", "");
                    operations.put("operationUrl", "");
                } else if ("ECS".equals(serviceName)) {
                    String policy = ServiceHelper.generatePolicyDocument(serviceName.toLowerCase(), Collections.singletonList(resource.getResourceId()), 3, CommonConstants.Aliyun_UserId);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                            CommonConstants.Aliyun_AccessKeySecret,
                            CommonConstants.ADMIN_ROLE_ARN, username, policy, false);
                    String terminalUrl = "https://ecs-workbench.aliyun.com/?instanceType=" + resource.getServiceName().toLowerCase() + "&regionId=" + resource.getRegionId() + "&instanceId=" + resource.getResourceId();
                    operations.put("operationName", "Terminal");

                    String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
                            + URLEncoder.encode(terminalUrl, StandardCharsets.UTF_8.displayName())
                            + "&LoginUrl=https%3a%2f%2faliyun.com&SigninToken="
                            + signToken;
                    operations.put("operationUrl", redirectUrl);
                } else { // RDS
                    String policy = ServiceHelper.generatePolicyDocument(serviceName.toLowerCase(), Collections.singletonList(resource.getResourceId()), 3, CommonConstants.Aliyun_UserId);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                            CommonConstants.Aliyun_AccessKeySecret,
                            CommonConstants.ADMIN_ROLE_ARN, username, policy, false);
                    String sqlConsoleUrl = "https://dms.aliyun.com/?regionId="+resource.getRegionId()+"&dbType=mysql&instanceId="+resource.getResourceId()+"&instanceSource="+resource.getServiceName();
                    operations.put("operationName", "SQL Console");

                    String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
                            + URLEncoder.encode(sqlConsoleUrl, StandardCharsets.UTF_8.displayName())
                            + "&LoginUrl=https%3a%2f%2faliyun.com&SigninToken="
                            + signToken;
                    operations.put("operationUrl", redirectUrl);
                }
                resource.setOperations(operations);
            }
        }
    }

    public static List<Event> listResourceEvents(String resourceId) throws Exception {
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
