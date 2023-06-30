package cc.landingzone.dreamweb;

import com.alibaba.fastjson.JSON;
import com.aliyun.ecs20140526.models.RunInstancesRequest;
import com.aliyun.ecs20140526.models.RunInstancesResponse;
import com.aliyun.tag20180828.models.TagResourcesRequest;
import com.aliyun.teautil.models.RuntimeOptions;

import java.util.*;

public class Ecs {

    public static void main(String[] args) throws Exception {
        String accountId = System.getenv("ALIBABA_CLOUD_ACCOUNT_ID");
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

        // 创建ECS
        com.aliyun.ecs20140526.Client client = createEcsClient(accessKeyId, accessKeySecret);
        RuntimeOptions runtime = new RuntimeOptions();
        RunInstancesRequest.RunInstancesRequestSystemDisk systemDisk = new RunInstancesRequest.RunInstancesRequestSystemDisk();
        systemDisk.setSize("40");
        // 系统盘类型: cloud_efficiency(高效云盘), cloud_ssd(SSD云盘), cloud_essd(ESSD云盘)
        systemDisk.setCategory("cloud_ssd");
        List<RunInstancesRequest.RunInstancesRequestDataDisk> dataDisks = new ArrayList<>();
        RunInstancesRequest.RunInstancesRequestDataDisk dataDisk = new RunInstancesRequest.RunInstancesRequestDataDisk();
        dataDisk.setSize(100);
        dataDisk.setCategory("cloud_ssd");
        dataDisks.add(dataDisk);
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .setRegionId("cn-hangzhou")
                .setAmount(2)
                .setInstanceType("ecs.n2.small")
                // 镜像ID
                .setImageId("ubuntu_18_04_64_20G_alibase_20190624.vhd")
                // 安全组ID
                .setSecurityGroupId("sg-bp103rdxtizwmfam0tfa")
                // 虚拟交换机ID,可用ID
                .setVSwitchId("vsw-bp1tipegxihb0brq0qy61")
                // 付费方式: PostPaid(按量付费), PrePaid(包年包月)
                .setInstanceChargeType("PostPaid")
                .setSystemDisk(systemDisk)
                .setDataDisk(dataDisks)
                // uuid: 标识唯一ECS
                .setHostName("ECS-test-" + UUID.randomUUID())
                // 为HostName和InstanceName自动添加有序后缀
                .setUniqueSuffix(true)
                .setPassword("ECS@test1234")
                // 出网带宽最大值: 单位为Mbps(Mega bit per second)
                .setInternetMaxBandwidthOut(10);
        RunInstancesResponse runInstancesResponse = client.runInstancesWithOptions(runInstancesRequest, runtime);
        List<String> instanceIdSet = runInstancesResponse.getBody().getInstanceIdSets().getInstanceIdSet();

        // 添加标签
        com.aliyun.tag20180828.Client tagClient = createTagClient(accessKeyId, accessKeySecret);
        Map<String, String> tags = new HashMap<>();
        tags.put("application", "application1");
        tags.put("environmentType", "product");
        String tagStr = JSON.toJSONString(tags);

        List<String> resourceArn = new ArrayList<>();
        for (String instanceId : instanceIdSet) {
            resourceArn.add("acs:ecs:*:" + accountId + ":instance/" + instanceId);
        }
        TagResourcesRequest tagResourcesRequest = new TagResourcesRequest()
                .setResourceARN(resourceArn)
                .setTags(tagStr)
                .setRegionId("cn-hangzhou");
        tagClient.tagResourcesWithOptions(tagResourcesRequest, runtime);
    }

    public static com.aliyun.ecs20140526.Client createEcsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("ecs-cn-hangzhou.aliyuncs.com");
        return new com.aliyun.ecs20140526.Client(config);
    }

    public static com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "tag.aliyuncs.com";
        return new com.aliyun.tag20180828.Client(config);
    }

}