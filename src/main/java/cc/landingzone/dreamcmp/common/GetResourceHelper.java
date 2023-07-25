package cc.landingzone.dreamcmp.common;

import com.aliyun.ecs20140526.models.DescribeInstancesResponseBody;
import com.aliyun.rds20140815.models.DescribeDBInstanceAttributeResponseBody;
import com.aliyun.rds20140815.models.DescribeDBInstancesResponseBody;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import com.aliyun.sdk.service.oss20190517.models.GetBucketInfoRequest;
import com.aliyun.sdk.service.oss20190517.models.GetBucketInfoResponse;
import com.aliyun.sdk.service.oss20190517.models.ListBucketsRequest;
import com.aliyun.sdk.service.oss20190517.models.ListBucketsResponse;
import com.aliyun.slb20140515.models.DescribeLoadBalancersResponseBody;
import com.aliyun.sls20201230.models.Project;

import cc.landingzone.dreamcmp.demo.appcenter.model.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class GetResourceHelper {
    // 一次获取多个资源的信息
    public static void setResourcesDetails(String appName, String serviceName, List<Resource> resources) throws Exception {
        if ("ECS".equals(serviceName)) {
            setEcsResourcesDetails(appName, resources);
        } else if ("OSS".equals(serviceName)) {
            setOssResourcesDetails(resources);
        } else if ("SLB".equals(serviceName)) {
            setSlbResourcesDetails(appName, resources);
        } else if ("RDS".equals(serviceName)) {
            setRdsResourcesDetails(appName, resources);
        } else {
            setSlsResourcesDetails(resources);
        }
    }

    private static void setEcsResourcesDetails(String appName, List<Resource> resources) throws Exception {
        Map<String, DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance> resourceDetails = new HashMap<>();
        com.aliyun.ecs20140526.Client client = ClientHelper.createEcsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

        com.aliyun.ecs20140526.models.DescribeInstancesRequest.DescribeInstancesRequestTag tag0 = new com.aliyun.ecs20140526.models.DescribeInstancesRequest.DescribeInstancesRequestTag()
                .setKey(CommonConstants.APPLICATION_TAG_KEY)
                .setValue(appName);

        com.aliyun.ecs20140526.models.DescribeInstancesRequest describeInstancesRequest = new com.aliyun.ecs20140526.models.DescribeInstancesRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTag(Collections.singletonList(tag0))
                .setPageNumber(1)
                .setPageSize(100);

        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance> instances = client
                .describeInstancesWithOptions(describeInstancesRequest, runtime).getBody().getInstances().getInstance();
        for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance instance: instances) {
            String resourceId = instance.getInstanceId();
            resourceDetails.put(resourceId, instance);
        }

        for (Resource resource: resources) {
            DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance instance = resourceDetails.get(resource.getResourceId());
            if (instance != null) {
                resource.setResourceName(instance.getInstanceName());
                resource.setCreateTime(instance.getCreationTime());
                resource.setRegionId(instance.getRegionId());
            }
        }
    }

    private static void setOssResourcesDetails(List<Resource> resources) throws Exception {
        Map<String, com.aliyun.sdk.service.oss20190517.models.Bucket> resourceDetails = new HashMap<>();
        AsyncClient client = ClientHelper.createOssClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder()
                .maxKeys(1000L)
                .build();
        CompletableFuture<ListBucketsResponse> response = client.listBuckets(listBucketsRequest);

        List<com.aliyun.sdk.service.oss20190517.models.Bucket> buckets = response.get().getBody().getBuckets();
        client.close();

        for (com.aliyun.sdk.service.oss20190517.models.Bucket bucket: buckets) {
            String resourceId = bucket.getName();
            resourceDetails.put(resourceId, bucket);
        }

        for (Resource resource: resources) {
            com.aliyun.sdk.service.oss20190517.models.Bucket bucket = resourceDetails.get(resource.getResourceId());
            if (bucket != null) {
                resource.setResourceName(bucket.getName());
                resource.setCreateTime(bucket.getCreationDate());
                resource.setRegionId(bucket.getRegion());
            }
        }
    }

    private static void setSlbResourcesDetails(String appName, List<Resource> resources) throws Exception {
        Map<String, DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer> resourceDetails = new HashMap<>();

        com.aliyun.slb20140515.Client client = ClientHelper.createSlbClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.slb20140515.models.DescribeLoadBalancersRequest describeLoadBalancersRequest = new com.aliyun.slb20140515.models.DescribeLoadBalancersRequest()
                .setPageNumber(1)
                .setPageSize(100)
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setTags("[{\"tagKey\":\"" + CommonConstants.APPLICATION_TAG_KEY + "\",\"tagValue\":\"" + appName + "\"}]");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer> loadBalancers = client
                .describeLoadBalancersWithOptions(describeLoadBalancersRequest, runtime).getBody().getLoadBalancers().getLoadBalancer();

        for (DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer loadBalancer: loadBalancers) {
            String resourceId = loadBalancer.getLoadBalancerId();
            resourceDetails.put(resourceId, loadBalancer);
        }

        for (Resource resource: resources) {
            DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer loadBalancer = resourceDetails.get(resource.getResourceId());
            if (loadBalancer != null) {
                resource.setResourceName(loadBalancer.getLoadBalancerName());
                resource.setCreateTime(loadBalancer.getCreateTime());
                resource.setRegionId(loadBalancer.getRegionId());
            }
        }
    }

    private static void setRdsResourcesDetails(String appName, List<Resource> resources) throws Exception {
        Map<String, DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> resourceDetails = new HashMap<>();

        com.aliyun.rds20140815.Client client = ClientHelper.createRdsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.rds20140815.models.DescribeDBInstancesRequest describeDBInstancesRequest = new com.aliyun.rds20140815.models.DescribeDBInstancesRequest()
                .setPageNumber(1)
                .setPageSize(100)
                .setTags("{\"" + CommonConstants.APPLICATION_TAG_KEY + "\":\"" + appName + "\"}")
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance> DBInstances = client
                .describeDBInstancesWithOptions(describeDBInstancesRequest, runtime).getBody().getItems().getDBInstance();

        for (DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance DBInstance: DBInstances) {
            String resourceId = DBInstance.getDBInstanceId();
            resourceDetails.put(resourceId, DBInstance);
        }

        for (Resource resource: resources) {
            DescribeDBInstancesResponseBody.DescribeDBInstancesResponseBodyItemsDBInstance DBInstance = resourceDetails.get(resource.getResourceId());
            if (DBInstance != null) {
                resource.setResourceName(DBInstance.getDBInstanceId());
                resource.setCreateTime(DBInstance.getCreateTime());
                resource.setRegionId(DBInstance.getRegionId());
            }
        }
    }

    private static void setSlsResourcesDetails(List<Resource> resources) throws Exception {
        Map<String, Project> resourceDetails = new HashMap<>();
        com.aliyun.sls20201230.Client client = ClientHelper.createSlsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.sls20201230.models.ListProjectRequest listProjectRequest = new com.aliyun.sls20201230.models.ListProjectRequest()
                .setSize(500);

        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        java.util.Map<String, String> headers = new java.util.HashMap<>();
        List<Project> projects = client.listProjectWithOptions("", listProjectRequest, headers, runtime).getBody().getProjects();

        for (Project project : projects) {
            String resourceId = project.getProjectName();
            resourceDetails.put(resourceId, project);
        }

        for (Resource resource : resources) {
            Project project = resourceDetails.get(resource.getResourceId());
            if (project != null) {
                resource.setResourceName(project.getProjectName());
                resource.setCreateTime(project.getCreateTime());
                resource.setRegionId(project.getRegion());
            }
        }
    }

    public static void getResourceDetail(Resource resource) throws Exception {
        String serviceName = resource.getServiceName();
        resource.setResourceType(ServiceEnum.valueOf(serviceName).getResourceType());
        if ("ECS".equals(serviceName)) {
            getEcsResource(resource);
        } else if ("OSS".equals(serviceName)) {
            getOssResource(resource);
        } else if ("SLB".equals(serviceName)) {
            getSlbResource(resource);
        } else if ("RDS".equals(serviceName)) {
            getRdsResource(resource);
        } else {
            getSlsResource(resource);
        }
    }

    private static void getEcsResource(Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        com.aliyun.ecs20140526.Client client = ClientHelper.createEcsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.ecs20140526.models.DescribeInstancesRequest describeInstancesRequest = new com.aliyun.ecs20140526.models.DescribeInstancesRequest()
                .setInstanceIds("[\""+resourceId+"\"]")
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance> instances = client
                .describeInstancesWithOptions(describeInstancesRequest, runtime).getBody().getInstances().getInstance();
        for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstance instance: instances) {
            resource.setResourceId(resourceId);
            resource.setResourceName(instance.getInstanceName());
            for (DescribeInstancesResponseBody.DescribeInstancesResponseBodyInstancesInstanceTagsTag tag: instance.getTags().getTag()) {
                if (CommonConstants.ENVIRONMENT_TYPE_TAG_KEY.equals(tag.getTagKey())) {
                    resource.setEnvironmentType(tag.getTagValue());
                }
            }
            resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            resource.setCreateTime(instance.getCreationTime());
        }
    }

    private static void getOssResource(Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        AsyncClient client = ClientHelper.createOssClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        GetBucketInfoRequest getBucketInfoRequest = GetBucketInfoRequest.builder()
                .bucket(resourceId)
                .build();
        CompletableFuture<GetBucketInfoResponse> response = client.getBucketInfo(getBucketInfoRequest);
        com.aliyun.sdk.service.oss20190517.models.GetBucketInfoResponseBody.BucketInfo bucketInfo = response.get().getBody().getBucketInfo();
        client.close();

        resource.setResourceId(resourceId);
        resource.setResourceName(bucketInfo.getName());
        resource.setEnvironmentType(""); // 查不到tag
        resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
        resource.setCreateTime(bucketInfo.getCreationDate());
    }

    private static void getSlbResource(Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        com.aliyun.slb20140515.Client client = ClientHelper.createSlbClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.slb20140515.models.DescribeLoadBalancersRequest describeLoadBalancersRequest = new com.aliyun.slb20140515.models.DescribeLoadBalancersRequest()
                .setLoadBalancerId(resourceId)
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer> loadBalancers = client
                .describeLoadBalancersWithOptions(describeLoadBalancersRequest, runtime).getBody().getLoadBalancers().getLoadBalancer();
        for (DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancer loadBalancer: loadBalancers) {
            resource.setResourceId(resourceId);
            resource.setResourceName(loadBalancer.getLoadBalancerName());
            for (DescribeLoadBalancersResponseBody.DescribeLoadBalancersResponseBodyLoadBalancersLoadBalancerTagsTag tag: loadBalancer.getTags().getTag()) {
                if (CommonConstants.ENVIRONMENT_TYPE_TAG_KEY.equals(tag.getTagKey())) {
                    resource.setEnvironmentType(tag.getTagValue());
                }
            }
            resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            resource.setCreateTime(loadBalancer.getCreateTime());
        }
    }

    private static void getRdsResource(Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        com.aliyun.rds20140815.Client client = ClientHelper.createRdsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.rds20140815.models.DescribeDBInstanceAttributeRequest describeDBInstanceAttributeRequest = new com.aliyun.rds20140815.models.DescribeDBInstanceAttributeRequest()
                .setDBInstanceId(resourceId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        List<DescribeDBInstanceAttributeResponseBody.DescribeDBInstanceAttributeResponseBodyItemsDBInstanceAttribute> DBInstanceAttributes = client
                .describeDBInstanceAttributeWithOptions(describeDBInstanceAttributeRequest, runtime).getBody().getItems().getDBInstanceAttribute();
        for (DescribeDBInstanceAttributeResponseBody.DescribeDBInstanceAttributeResponseBodyItemsDBInstanceAttribute DBInstanceAttribute: DBInstanceAttributes) {
            resource.setResourceId(resourceId);
            resource.setResourceName(DBInstanceAttribute.getDBInstanceId()); // DBInstance没有name
            resource.setEnvironmentType(""); // 查不到tag
            resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            resource.setCreateTime(DBInstanceAttribute.getCreationTime());
        }
    }

    private static void getSlsResource(Resource resource) throws Exception {
        String resourceId = resource.getResourceId();
        com.aliyun.sls20201230.Client client = ClientHelper.createSlsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        java.util.Map<String, String> headers = new java.util.HashMap<>();
        Project project = client.getProjectWithOptions(resourceId, headers, runtime).getBody();

        resource.setResourceId(resourceId);
        resource.setResourceName(project.getProjectName());
        resource.setEnvironmentType(""); // 查不到tag
        resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
        resource.setCreateTime(project.getCreateTime());
    }
}
