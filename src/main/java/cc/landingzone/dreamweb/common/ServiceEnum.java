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

public enum ServiceEnum {
    /**
     * ECS
     */
    ECS("ALIYUN::ECS::INSTANCE"),
    /**
     * OSS
     */
    OSS("ALIYUN::OSS::BUCKET"),
    /**
     * SLB
     */
    SLB("ALIYUN::SLB::INSTANCE"),
    /**
     * RDS
     */
    RDS("ALIYUN::RDS::INSTANCE"),
    /**
     * SLS
     */
    SLS("ALIYUN::LOG::PROJECT");



    private String resourceType;

    ServiceEnum(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public List<Resource> getResources(List<String> resourceIds) throws Exception {
        List<Resource> resources = new ArrayList<>();
        for (String resourceId: resourceIds) {
            if (ECS == this) {
                resources.add(getEcsResource(resourceId));
            } else if (OSS == this) {
                resources.add(getOssResource(resourceId));
            } else if (SLB == this) {
                resources.add(getSlbResource(resourceId));
            } else if (RDS == this) {
                resources.add(getRdsResource(resourceId));
            } else {
                resources.add(getSlsResource(resourceId));
            }
        }
        return resources;
    }

    private Resource getEcsResource(String resourceId) throws Exception {
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

    private Resource getOssResource(String resourceId) throws Exception {
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

    private Resource getSlbResource(String resourceId) throws Exception {
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

    private Resource getRdsResource(String resourceId) throws Exception {
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

    private Resource getSlsResource(String resourceId) throws Exception {
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
