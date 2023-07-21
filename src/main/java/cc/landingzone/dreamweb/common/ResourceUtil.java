package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamweb.demo.appcenter.model.Event;
import cc.landingzone.dreamweb.demo.appcenter.model.Resource;
import com.aliyun.ecs20140526.models.DescribeInstancesResponseBody;
import com.aliyun.resourcecenter20221201.models.SearchMultiAccountResourcesResponseBody;
import com.aliyun.resourcemanager20200331.models.ListAccountsResponseBody;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
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
        com.aliyun.tag20180828.Client client = ClientHelper.createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

        com.aliyun.tag20180828.models.ListTagResourcesRequest listTagResourcesRequest = new com.aliyun.tag20180828.models.ListTagResourcesRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTags("{\"" + tagKey + "\":\"" + tagValue + "\"}")
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

        // ECS需要显示name，所以额外调用一次ECS自己的API
        com.aliyun.ecs20140526.Client client = ClientHelper.createEcsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.ecs20140526.models.DescribeInstancesRequest.DescribeInstancesRequestTag tag0 = new com.aliyun.ecs20140526.models.DescribeInstancesRequest.DescribeInstancesRequestTag()
                .setKey(CommonConstants.APPLICATION_TAG_KEY)
                .setValue(appName);
        com.aliyun.ecs20140526.models.DescribeInstancesRequest describeInstancesRequest = new com.aliyun.ecs20140526.models.DescribeInstancesRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTag(Collections.singletonList(tag0));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        List<DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance> instances = client.describeInstancesWithOptions(describeInstancesRequest, runtime).getBody().getInstances().getInstance();
        for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance instance: instances) {
            Resource resource = new Resource();
            String serviceName = "ECS";
            String resourceId = instance.getInstanceId();
            String resourceName = instance.getInstanceName();
            String environmentType = "";
            List<DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstanceTagsTag> tags = instance.getTags().getTag();
            for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstanceTagsTag tag: tags) {
                 if (tag.getTagKey().equals(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY)) {
                     environmentType = tag.getTagValue();
                 }
            }
            resource.setServiceName(serviceName);
            resource.setResourceId(resourceId + " | " + resourceName);
            resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            resource.setEnvironmentType(environmentType);
            resources.add(resource);
        }

        List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = getResourcesByTag(CommonConstants.APPLICATION_TAG_KEY, appName);

        for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resourceResponse : resourcesResponse) {
            // ARN format: arn:acs:${Service}:${Region}:${Account}:${ResourceType(e.g.instance)}/${ResourceId}
            String[] splitArn = resourceResponse.getResourceARN().split(":");
            String serviceName = splitArn[2].toUpperCase();
            if ("LOG".equals(serviceName)) {
                serviceName = "SLS";
            }
            if ("ECS".equals(serviceName)) {
                continue;
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

        for (ServiceEnum serviceEnum : ServiceEnum.values()) {
            servicesResources.put(serviceEnum.name(), new ArrayList<>());
        }

        for (Resource resource : resources) {
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

        for (String environmentType : CommonConstants.ENVIRONMENT_TYPE_TAG_VALUES) {
            List<ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources> resourcesResponse = getResourcesByTag(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY, environmentType);
            for (ListTagResourcesResponseBody.ListTagResourcesResponseBodyTagResources resourceResponse : resourcesResponse) {
                String[] splitArn = resourceResponse.getResourceARN().split(":");
                String resourceId = splitArn[5].split("/")[1];
                resourceEnvType.put(resourceId, environmentType);
            }
        }

        for (Resource resource : resources) {
            String envType = resourceEnvType.get(resource.getResourceId());
            if (StringUtils.isNotBlank(envType)) {
                resource.setEnvironmentType(envType);
            }
        }
    }

    /**
     * 获得ECS和RDS的特殊操作url
     *
     * @param servicesResources
     */
    public static void setOperations(Map<String, List<Resource>> servicesResources) throws Exception {
        for (String serviceName : servicesResources.keySet()) {
            ServiceEnum serviceEnum = ServiceEnum.valueOf(serviceName);
            for (Resource resource : servicesResources.get(serviceName)) {
                resource.setResourceType(serviceEnum.getResourceType());
                Map<String, String> operations = new HashMap<>();
                if (!"ECS".equals(serviceName) && !"RDS".equals(serviceName)) {
                    operations.put("operationName", "");
                    operations.put("operationUrl", "");
                } else if ("ECS".equals(serviceName)) {
//                    String policy = ServiceHelper.generatePolicyDocument(serviceName.toLowerCase(), Collections.singletonList(resource.getResourceId()), 3, CommonConstants.Aliyun_UserId);
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                            CommonConstants.Aliyun_AccessKeySecret,
                            CommonConstants.ADMIN_ROLE_ARN, username, "", false);
                    String terminalUrl = "https://ecs-workbench.aliyun.com/?instanceType=" + resource.getServiceName().toLowerCase() + "&regionId=" + resource.getRegionId() + "&instanceId=" + resource.getResourceId().split(" | ")[0];
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
                    String sqlConsoleUrl = "https://dms.aliyun.com/?regionId=" + resource.getRegionId() + "&dbType=mysql&instanceId=" + resource.getResourceId() + "&instanceSource=" + resource.getServiceName();
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

        com.aliyun.actiontrail20200706.Client client = ClientHelper.createTrailClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.actiontrail20200706.models.LookupEventsRequest.LookupEventsRequestLookupAttribute lookupAttribute0 = new com.aliyun.actiontrail20200706.models.LookupEventsRequest.LookupEventsRequestLookupAttribute()
                .setKey("ResourceName")
                .setValue(resourceId);
        com.aliyun.actiontrail20200706.models.LookupEventsRequest lookupEventsRequest = new com.aliyun.actiontrail20200706.models.LookupEventsRequest()
                .setLookupAttribute(Collections.singletonList(lookupAttribute0));
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();


        List<Map<String, ?>> detailedEvents = client.lookupEventsWithOptions(lookupEventsRequest, runtime).getBody().getEvents();
        for (Map<String, ?> detailedEvent : detailedEvents) {
            Event event = new Event();
            event.setEventTime(detailedEvent.get("eventTime").toString());
            event.setEventName(detailedEvent.get("eventName").toString());
            event.setUserIdentity(detailedEvent.get("userIdentity"));
            events.add(event);
        }

        return events;
    }

    public static String getResourceDirectoryId() throws Exception {
        com.aliyun.resourcemanager20200331.Client client = ClientHelper.createResourceManagerClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        return client.getResourceDirectoryWithOptions(runtime).getBody().getResourceDirectory().getResourceDirectoryId();
    }

    /**
     * 获取accountId及其对应的accountName, 不包括管理账号
     *
     * @return Map<String, String> accountNames
     * @throws Exception
     */
    public static Map<String, String> getAccountNames() throws Exception {
        Map<String, String> accountNames = new HashMap<>();
        com.aliyun.resourcemanager20200331.Client client = ClientHelper.createResourceManagerClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        com.aliyun.resourcemanager20200331.models.ListAccountsRequest listAccountsRequest = new com.aliyun.resourcemanager20200331.models.ListAccountsRequest();
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        List<ListAccountsResponseBody.ListAccountsResponseBodyAccountsAccount> accounts =
                client.listAccountsWithOptions(listAccountsRequest, runtime).getBody().getAccounts().getAccount();
        for (ListAccountsResponseBody.ListAccountsResponseBodyAccountsAccount account : accounts) {
            String accountId = account.getAccountId();
            String accountName = account.getDisplayName();
            accountNames.put(accountId, accountName);
        }
        return accountNames;
    }

    public static Map<String, Map<String, Map<String, Integer>>> listAccountRegionResourcesCounts(String resourceDirectoryId) throws Exception {
        Map<String, Map<String, Map<String, Integer>>> accountRegionResourcesCounts = new HashMap<>();

        String nextToken = null;
        List<SearchMultiAccountResourcesResponseBody.SearchMultiAccountResourcesResponseBodyResources> resources = new ArrayList<>();

        com.aliyun.resourcecenter20221201.Client client = ClientHelper.createResourceCenterClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);

        do {
            com.aliyun.resourcecenter20221201.models.SearchMultiAccountResourcesRequest searchMultiAccountResourcesRequest = new com.aliyun.resourcecenter20221201.models.SearchMultiAccountResourcesRequest()
                    .setScope(resourceDirectoryId)
                    .setMaxResults(100)
                    .setNextToken(nextToken);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

            com.aliyun.resourcecenter20221201.models.SearchMultiAccountResourcesResponseBody searchMultiAccountResourcesResponseBody = client.searchMultiAccountResourcesWithOptions(searchMultiAccountResourcesRequest, runtime).getBody();

            nextToken = searchMultiAccountResourcesResponseBody.getNextToken();
            resources.addAll(searchMultiAccountResourcesResponseBody.getResources());
        } while (nextToken != null);

        Map<String, String> accountNames = getAccountNames();

        for (SearchMultiAccountResourcesResponseBody.SearchMultiAccountResourcesResponseBodyResources resource : resources) {
            String accountId = resource.getAccountId();
            String accountName;
            if (accountId.equals(CommonConstants.Aliyun_TestAccount_UserId)) {
                accountName = CommonConstants.RESOURCE_CENTER_ADMIN_NAME;
            } else {
                accountName = accountNames.get(accountId);
            }

            String regionId = resource.getRegionId();
            String serviceResourceType = resource.getResourceType();

            if (accountRegionResourcesCounts.get(accountName) == null) {
                Map<String, Map<String, Integer>> regionResourcesCounts = new HashMap<>();
                accountRegionResourcesCounts.put(accountName, regionResourcesCounts);
            }
            if (accountRegionResourcesCounts.get(accountName).get(regionId) == null) {
                Map<String, Integer> resourcesCounts = new HashMap<>(ServiceEnum.values().length);
                for (ServiceEnum serviceEnum : ServiceEnum.values()) {
                    resourcesCounts.put(serviceEnum.name(), 0);
                }
                accountRegionResourcesCounts.get(accountName).put(regionId, resourcesCounts);
            }

            // add up resources to resource directory id
            if (accountRegionResourcesCounts.get(resourceDirectoryId) == null) {
                Map<String, Map<String, Integer>> regionResourcesCounts = new HashMap<>();
                accountRegionResourcesCounts.put(resourceDirectoryId, regionResourcesCounts);
            }
            if (accountRegionResourcesCounts.get(resourceDirectoryId).get(regionId) == null) {
                Map<String, Integer> resourcesCounts = new HashMap<>(ServiceEnum.values().length);
                for (ServiceEnum serviceEnum : ServiceEnum.values()) {
                    resourcesCounts.put(serviceEnum.name(), 0);
                }
                accountRegionResourcesCounts.get(resourceDirectoryId).put(regionId, resourcesCounts);
            }

            Map<String, Integer> resourcesCounts = accountRegionResourcesCounts.get(accountName).get(regionId);
            Map<String, Integer> directoryResourcesCounts = accountRegionResourcesCounts.get(resourceDirectoryId).get(regionId);

            String serviceName = serviceResourceType.split("::")[1];
            String resourceType = serviceResourceType.split("::")[2].toLowerCase();
            try {
                ServiceEnum serviceEnum = ServiceEnum.valueOf(serviceName);
                // resourceType要为特定类型，如：ecs instance
                // 但slb和rds返回的ResourceType参数值与ServiceEnum中resourceType的值不一致，这里暂时hard code
                if (resourcesCounts.get(serviceName) != null &&
                        (resourceType.equals(serviceEnum.getResourceType().split("::")[2].toLowerCase()) ||
                                "SLB".equals(serviceName) && "loadbalancer".equals(resourceType) ||
                                "RDS".equals(serviceName) && "dbinstance".equals(resourceType))) {
                    resourcesCounts.merge(serviceName, 1, Integer::sum);
                }
                if (directoryResourcesCounts.get(serviceName) != null &&
                        (resourceType.equals(serviceEnum.getResourceType().split("::")[2].toLowerCase()) ||
                                "SLB".equals(serviceName) && "loadbalancer".equals(resourceType) ||
                                "RDS".equals(serviceName) && "dbinstance".equals(resourceType))) {
                    directoryResourcesCounts.merge(serviceName, 1, Integer::sum);
                }
            } catch (Exception ignored) {

            }
        }

        return accountRegionResourcesCounts;
    }

    public static List<String> listAccountsWithoutResources(Map<String, Map<String, Map<String, Integer>>> accountRegionResourcesCounts) throws Exception {
        Map<String, String> accountNames = getAccountNames();
        List<String> accountsWithoutResources = new ArrayList<>();
        for (Map.Entry<String, String> accountIdName : accountNames.entrySet()) {
            if (accountRegionResourcesCounts.get(accountIdName.getValue()) == null) {
                accountsWithoutResources.add(accountIdName.getValue());
            }
        }
        return accountsWithoutResources;
    }


    public static String getSLSProjectNameByAppName(String appName) throws Exception {
        com.aliyun.tag20180828.Client client = ClientHelper.createTagClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter tagFilter = new com.aliyun.tag20180828.models.ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter()
                .setKey("application")
                .setValue(appName);
        com.aliyun.tag20180828.models.ListResourcesByTagRequest listResourcesByTagRequest = new com.aliyun.tag20180828.models.ListResourcesByTagRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setResourceType(ServiceEnum.SLS.getResourceType())
                .setTagFilter(tagFilter);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        List<ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResources> projects = client.listResourcesByTagWithOptions(listResourcesByTagRequest, runtime).getBody().getResources();
        if (projects.size() == 1) {
            return projects.get(0).getResourceId();
        } else {
            // 如果有多个projects，选择特定名字的project，保证演示时project有内容
            return CommonConstants.SLS_PROJECT_NAME;
        }
    }
}
