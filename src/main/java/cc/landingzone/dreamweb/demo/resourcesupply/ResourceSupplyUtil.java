package cc.landingzone.dreamweb.demo.resourcesupply;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceEnum;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.demo.akapply.AkApplyUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.ecs20140526.models.DescribeInstanceTypeFamiliesRequest;
import com.aliyun.ecs20140526.models.RunInstancesRequest;
import com.aliyun.ecs20140526.models.RunInstancesResponse;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import com.aliyun.sdk.service.oss20190517.models.CreateBucketConfiguration;
import com.aliyun.sdk.service.oss20190517.models.PutBucketRequest;
import com.aliyun.sdk.service.oss20190517.models.PutBucketResponse;
import com.aliyun.sls20201230.Client;
import com.aliyun.sls20201230.models.CreateProjectRequest;
import com.aliyun.tag20180828.models.TagResourcesRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import com.aliyun.vpc20160428.models.DescribeVSwitchesRequest;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;


public class ResourceSupplyUtil {

    public static Logger logger = LoggerFactory.getLogger(AkApplyUtil.class);

    public static void main(String[] args) {
//       String regionId = "cn-hangzhou";
//       String vSwitchId = "vsw-bp1xbv82k61y8izq0wq3h";
//       String instanceType = "ecs.i2.xlarge";
//       int amount = 2;
//       createEcsInstance(regionId,vSwitchId,instanceType,amount);

//        createOssBucket("buckettestapi1");

        System.out.println(ServiceEnum.ECS.name());

//        String regionId = "cn-hangzhou";
//        String generation = "ecs-3";
//        System.out.println(describeInstanceTypeFamilies(regionId, generation));
    }

    public static String createEcsInstance(String regionId, String vSwitchId, String instanceType, int amount,
                                           String applicationName,String environmentName) {
        try {
            com.aliyun.ecs20140526.Client client = ServiceHelper.createEcsClient
                    (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
            RuntimeOptions runtime = new RuntimeOptions();

            RunInstancesRequest.RunInstancesRequestSystemDisk systemDisk = new RunInstancesRequest.RunInstancesRequestSystemDisk();
            systemDisk.setSize(CommonConstants.DEFAULT_ECS_SYSTEM_DISK_SIZE);
            // 系统盘类型: cloud_efficiency(高效云盘), cloud_ssd(SSD云盘), cloud_essd(ESSD云盘)
            systemDisk.setCategory(CommonConstants.DEFAULT_ECS_SYSTEM_DISK_CATEGORY);
            List<RunInstancesRequest.RunInstancesRequestDataDisk> dataDisks = new ArrayList<>();
            RunInstancesRequest.RunInstancesRequestDataDisk dataDisk = new RunInstancesRequest.RunInstancesRequestDataDisk();
            dataDisk.setSize(Integer.valueOf(CommonConstants.DEFAULT_ECS_DATA_DISK_SIZE));
            dataDisk.setCategory(CommonConstants.DEFAULT_ECS_DATA_DISK_CATEGORY);
            dataDisks.add(dataDisk);

            RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                    .setRegionId(regionId)
                    .setAmount(amount)
                    .setInstanceType(instanceType)
                    // 镜像ID
                    .setImageId(CommonConstants.DEFAULT_IMAGE_ID)
                    // 安全组ID
                    .setSecurityGroupId(CommonConstants.DEFAULT_SECURITY_GROUP_ID)
                    // 虚拟交换机ID,可用ID
                    .setVSwitchId(vSwitchId)
                    // 付费方式: PostPaid(按量付费), PrePaid(包年包月)
                    .setInstanceChargeType(CommonConstants.ECS_CHARGETYPE_POSTPAID)
                    .setSystemDisk(systemDisk)
                    .setDataDisk(dataDisks)
                    // uuid: 标识唯一ECS
                    .setHostName(CommonConstants.DEFAULT_ECS_HOSTNAME + UUID.randomUUID())
                    // 为HostName和InstanceName自动添加有序后缀
                    .setUniqueSuffix(true)
                    .setPassword(CommonConstants.DEFAULT_ECS_PASSWORD)
                    // 出网带宽最大值: 单位为Mbps(Mega bit per second)
                    .setInternetMaxBandwidthOut(Integer.valueOf(CommonConstants.DEFAULT_ECS_MAX_BANDWIDTH_OUT));

            RunInstancesResponse runInstancesResponse = client.runInstancesWithOptions(runInstancesRequest, runtime);
            List<String> instanceIdSet = runInstancesResponse.getBody().getInstanceIdSets().getInstanceIdSet();
            logger.info("instanceIdSet:{}", JSON.toJSONString(instanceIdSet));
            attachTagToResource(applicationName,environmentName,ServiceEnum.ECS.getResourceName(),
                    instanceIdSet);
            return "success";
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public static String createOssBucket(String bucketName,String applicationName,String environmentName) {
        try {

            AsyncClient client = ServiceHelper.createOssClient
                    (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);

            // 验证bucketName是否存在


            CreateBucketConfiguration createBucketConfiguration = CreateBucketConfiguration.builder()
                    .storageClass("Standard")
                    .dataRedundancyType("LRS")
                    .build();
            PutBucketRequest putBucketRequest = PutBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(createBucketConfiguration)
                    .build();

            CompletableFuture<PutBucketResponse> response = client.putBucket(putBucketRequest);
            PutBucketResponse resp = response.get();
            System.out.println(new Gson().toJson(resp));
            client.close();
            attachTagToResource(applicationName,environmentName,ServiceEnum.OSS.getResourceName(),
                    Arrays.asList(bucketName));
            return "success";
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public static String createLogProject(String projectName,String description,String applicationName,String environmentName) {
        try {
            Client client = ServiceHelper.createSlsClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
            //查询logProject是否存在

            // 创建logProject
            RuntimeOptions runtime = new RuntimeOptions();
            CreateProjectRequest createProjectRequest = new CreateProjectRequest()
                    .setDescription(description)
                    .setProjectName(projectName);
            Map<String, String> headers = new HashMap<>();
            client.createProjectWithOptions(createProjectRequest, headers, runtime);
            attachTagToResource(applicationName,environmentName,ServiceEnum.SLS.getResourceName(),Arrays.asList(projectName));
            return "success";
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public static void attachTagToResource(String applicationName, String environment, String resourceType,
                                           List<String> resourceNameList) throws Exception {

            Map<String,String> tags = new HashMap<>();
            tags.put(CommonConstants.APPLICATION_TAG_KEY, applicationName);
            tags.put(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY, environment);
            String tagStr = JSON.toJSONString(tags);
            logger.info("tagStr:{}", tagStr);
            com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient
                    (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        List<String> resourceArn = ServiceHelper.getResourceArnInTag
                (resourceType, resourceNameList, CommonConstants.Aliyun_UserId);
        logger.info("resourceArn:{}", JSON.toJSONString(resourceArn));
        TagResourcesRequest tagResourcesRequest = new TagResourcesRequest()
                    .setResourceARN(resourceArn)
                    .setTags(tagStr)
                    .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            RuntimeOptions runtime = new RuntimeOptions();
            client.tagResourcesWithOptions(tagResourcesRequest, runtime);
    }

    /**
     * 查询云服务器ECS提供的实例规格族列表
     * @param regionId
     * @param generation
     * @return
     */
    public static List<String> describeInstanceTypeFamilies(String regionId,String generation){
        try {
            com.aliyun.ecs20140526.Client client = ServiceHelper.createEcsClient
                    (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
            RuntimeOptions runtime = new RuntimeOptions();
            DescribeInstanceTypeFamiliesRequest describeInstanceTypeFamiliesRequest = new DescribeInstanceTypeFamiliesRequest()
                    .setRegionId(regionId)
                    .setGeneration(generation);
            List<String> instanceTypeFamilies = new ArrayList<>();
            client.describeInstanceTypeFamiliesWithOptions(describeInstanceTypeFamiliesRequest, runtime).getBody().
                    getInstanceTypeFamilies().getInstanceTypeFamily().forEach((instanceTypeFamily) -> {
                instanceTypeFamilies.add(instanceTypeFamily.getInstanceTypeFamilyId());
                    });
            return instanceTypeFamilies;
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public static List<String> describeVSwitches(){
        try {
            com.aliyun.vpc20160428.Client client = ServiceHelper.createVpcClient
                    (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
            RuntimeOptions runtime = new RuntimeOptions();
            DescribeVSwitchesRequest describeVSwitchesRequest = new DescribeVSwitchesRequest()
                    .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU);
            List<String> vSwitchList = new ArrayList<>();
            client.describeVSwitchesWithOptions(describeVSwitchesRequest, runtime).getBody().
                    getVSwitches().getVSwitch().forEach((vSwitch) -> {
                vSwitchList.add(vSwitch.getVSwitchName() + " / " + vSwitch.getVSwitchId());
            });
            return vSwitchList;
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ArrayList<>();
        }
    }

}