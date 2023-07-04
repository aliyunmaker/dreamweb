package cc.landingzone.dreamweb.common;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import com.aliyun.tag20180828.models.ListResourcesByTagRequest;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.aliyun.vpc20160428.models.DescribeVSwitchAttributesRequest;
import com.aliyun.vpc20160428.models.DescribeVpcAttributeRequest;
import com.aliyun.vpc20160428.models.DescribeVpcAttributeResponseBody;
import darabonba.core.client.ClientOverrideConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ServiceHelper {

    private static Logger logger = LoggerFactory.getLogger(ServiceHelper.class);

    public static com.aliyun.ecs20140526.Client createEcsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("ecs-cn-hangzhou.aliyuncs.com");
        return new com.aliyun.ecs20140526.Client(config);
    }

    public static com.aliyun.sdk.service.oss20190517.AsyncClient createOssClient(String accessKeyId, String accessKeySecret) throws Exception {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        return AsyncClient.builder()
                .region(CommonConstants.Aliyun_REGION_HANGZHOU)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("oss-cn-hangzhou.aliyuncs.com")
                )
                .build();
    }

    public static com.aliyun.slb20140515.Client createSlbClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("slb.aliyuncs.com");
        return new com.aliyun.slb20140515.Client(config);
    }

    public static com.aliyun.rds20140815.Client createRdsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("rds.aliyuncs.com");
        return new com.aliyun.rds20140815.Client(config);
    }

    public static com.aliyun.sls20201230.Client createSlsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("cn-hangzhou.log.aliyuncs.com");
        return new com.aliyun.sls20201230.Client(config);
    }

    public static com.aliyun.ram20150501.Client createRamClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "ram.aliyuncs.com";
        return new com.aliyun.ram20150501.Client(config);
    }

    public static com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "tag.aliyuncs.com";
        return new com.aliyun.tag20180828.Client(config);
    }

    public static com.aliyun.actiontrail20200706.Client createTrailClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "actiontrail.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.actiontrail20200706.Client(config);
    }

    public static com.aliyun.vpc20160428.Client createVpcClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "vpc.aliyuncs.com";
        return new com.aliyun.vpc20160428.Client(config);
    }


    /**
     * 权限策略 拼接资源ARN
     *
     * @param resourceType:oss、log
     */
    public static List<String> getResourceArnInPolicy(String resourceType, List<String> resourceNameList, String accountId) {
        List<String> resourceArn = new ArrayList<>();
        switch (resourceType) {
            case "oss":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:oss:*:" + accountId + ":" + resourceName);
                }
                break;
            case "log":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:log:*:" + accountId + ":project/" + resourceName);
                }
                break;
            default:
                break;
        }
        return resourceArn;
    }

    /**
     * tag标签 拼接资源ARN
     *
     * @param resourceType:ecs、oss、log
     */
    public static List<String> getResourceArnInTag(String resourceType, List<String> resourceNameList, String accountId) {
        List<String> resourceArn = new ArrayList<>();
        switch (resourceType) {
            case "ecs":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:ecs:*:" +
                            accountId + ":instance/" + resourceName);
                }
                break;
            case "oss":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:oss:*:" +
                            accountId + ":bucket/" + resourceName);
                }
                break;
            case "log":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:log:*:" +
                            accountId + ":project/" + resourceName);
                }
                break;
            default:
                break;
        }
        return resourceArn;
    }

    /**
     * 拼接RAM用户的ARN：acs:ram::<account-id>:user/<user-name>
     *
     * @param userName
     * @param accountId
     * @return
     */
    public static String getRamArn(String userName, String accountId) {
        return "acs:ram::" + accountId + ":user/" + userName;
    }


    /**
     * 基于标签查询资源
     *
     * @param applicationName
     * @param environment
     * @param resourceType
     * @return
     */
    public static List<String> listResourcesByTag(String applicationName, String environment, String resourceType) throws Exception {
        com.aliyun.tag20180828.Client client = ServiceHelper.createTagClient
                (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter tagFilter = new ListResourcesByTagRequest.ListResourcesByTagRequestTagFilter()
                .setValue(applicationName)
                .setKey(CommonConstants.APPLICATION_TAG_KEY);
        ListResourcesByTagRequest listResourcesByTagRequest = new ListResourcesByTagRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setMaxResult(1000)
                .setResourceType(resourceType)
                .setIncludeAllTags(true)
                .setTagFilter(tagFilter);
        RuntimeOptions runtime = new RuntimeOptions();
        List<String> resourceIds = new ArrayList<>();
        List<ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResources> resources = client.
                listResourcesByTagWithOptions(listResourcesByTagRequest, runtime).getBody().getResources();
        for (ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResources resource : resources) {
            for (ListResourcesByTagResponseBody.ListResourcesByTagResponseBodyResourcesTags tag : resource.tags) {
                if (tag.key.equals(CommonConstants.ENVIRONMENT_TYPE_TAG_KEY) && tag.value.equals(environment)) {
                    resourceIds.add(resource.resourceId);
                }
            }
        }
        return resourceIds;

    }

    /**
     * 查询指定交换机的配置信息,返回交换机信息
     *
     * @param vSwitchId
     */
    public static com.aliyun.vpc20160428.models.DescribeVSwitchAttributesResponseBody describeVSwitchAttribute(String vSwitchId) throws Exception {
        com.aliyun.vpc20160428.Client client = createVpcClient
                (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        DescribeVSwitchAttributesRequest describeVSwitchAttributesRequest = new DescribeVSwitchAttributesRequest()
                .setRegionId("cn-hangzhou")
                .setVSwitchId(vSwitchId)
                .setDryRun(false);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.describeVSwitchAttributesWithOptions(describeVSwitchAttributesRequest, runtime)
                .getBody();
    }

    /**
     * 查询指定VPC的配置信息,返回VPC信息
     *
     * @param vpcId
     */
    public static DescribeVpcAttributeResponseBody describeVpcAttribute(String vpcId) throws Exception {
        com.aliyun.vpc20160428.Client client = createVpcClient
                (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        DescribeVpcAttributeRequest describeVpcAttributeRequest = new DescribeVpcAttributeRequest()
                .setVpcId(vpcId)
                .setRegionId("cn-hangzhou");
        RuntimeOptions runtime = new RuntimeOptions();
        return client.describeVpcAttributeWithOptions(describeVpcAttributeRequest, runtime).getBody();
    }


}
