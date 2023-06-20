package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.demo.appcenter.model.Resource;
import com.aliyun.ecs20140526.models.DescribeInstancesResponseBody;
import com.aliyun.rds20140815.models.DescribeDBInstanceAttributeResponseBody;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import com.aliyun.sdk.service.oss20190517.models.GetBucketInfoRequest;
import com.aliyun.sdk.service.oss20190517.models.GetBucketInfoResponse;
import com.aliyun.slb20140515.models.DescribeLoadBalancersResponseBody;
import com.aliyun.sls20201230.models.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetResourceHelper {
    public static List<Resource> getResources(List<String> resourceIds, ServiceEnum serviceEnum) throws Exception {
        List<Resource> resources = new ArrayList<>();
        for (String resourceId: resourceIds) {
            if (serviceEnum == ServiceEnum.valueOf("ECS")) {
                resources.add(getEcsResource(resourceId));
            } else if (serviceEnum == ServiceEnum.valueOf("OSS")) {
                resources.add(getOssResource(resourceId));
            } else if (serviceEnum == ServiceEnum.valueOf("SLB")) {
                resources.add(getSlbResource(resourceId));
            } else if (serviceEnum == ServiceEnum.valueOf("RDS")) {
                resources.add(getRdsResource(resourceId));
            } else {
                resources.add(getSlsResource(resourceId));
            }
        }
        return resources;
    }

    private static Resource getEcsResource(String resourceId) throws Exception {
        Resource resource = new Resource();
        com.aliyun.ecs20140526.Client client = ServiceHelper.createEcsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.ecs20140526.models.DescribeInstancesRequest describeInstancesRequest = new com.aliyun.ecs20140526.models.DescribeInstancesRequest()
                .setInstanceIds(resourceId);
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
        return resource;
    }

    private static Resource getOssResource(String resourceId) throws Exception {
        Resource resource = new Resource();
        AsyncClient client = ServiceHelper.createOssClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
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

        return resource;
    }

    private static Resource getSlbResource(String resourceId) throws Exception {
        Resource resource = new Resource();
        com.aliyun.slb20140515.Client client = ServiceHelper.createSlbClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.slb20140515.models.DescribeLoadBalancersRequest describeLoadBalancersRequest = new com.aliyun.slb20140515.models.DescribeLoadBalancersRequest()
                .setLoadBalancerId(resourceId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        com.aliyun.slb20140515.models.DescribeLoadBalancersResponse response = client.describeLoadBalancersWithOptions(describeLoadBalancersRequest, runtime);

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
        return resource;
    }

    private static Resource getRdsResource(String resourceId) throws Exception {
        Resource resource = new Resource();
        com.aliyun.rds20140815.Client client = ServiceHelper.createRdsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.rds20140815.models.DescribeDBInstanceAttributeRequest describeDBInstanceAttributeRequest = new com.aliyun.rds20140815.models.DescribeDBInstanceAttributeRequest()
                .setDBInstanceId(resourceId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        com.aliyun.rds20140815.models.DescribeDBInstanceAttributeResponse response = client.describeDBInstanceAttributeWithOptions(describeDBInstanceAttributeRequest, runtime);

        List<DescribeDBInstanceAttributeResponseBody.DescribeDBInstanceAttributeResponseBodyItemsDBInstanceAttribute> DBInstanceAttributes = client
                .describeDBInstanceAttributeWithOptions(describeDBInstanceAttributeRequest, runtime).getBody().getItems().getDBInstanceAttribute();
        for (DescribeDBInstanceAttributeResponseBody.DescribeDBInstanceAttributeResponseBodyItemsDBInstanceAttribute DBInstanceAttribute: DBInstanceAttributes) {
            resource.setResourceId(resourceId);
            resource.setResourceName(DBInstanceAttribute.getDBInstanceId()); // DBInstance没有name
            resource.setEnvironmentType(""); // 查不到tag
            resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            resource.setCreateTime(DBInstanceAttribute.getCreationTime());
        }
        return resource;
    }

    private static Resource getSlsResource(String resourceId) throws Exception {
        Resource resource = new Resource();
        com.aliyun.sls20201230.Client client = ServiceHelper.createSlsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        java.util.Map<String, String> headers = new java.util.HashMap<>();
        Project project = client.getProjectWithOptions(resourceId, headers, runtime).getBody();

        resource.setResourceId(resourceId);
        resource.setResourceName(project.getProjectName());
        resource.setEnvironmentType(""); // 查不到tag
        resource.setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
        resource.setCreateTime(project.getCreateTime());

        return resource;
    }
}
