package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.demo.akapply.model.policytemplate.Condition;
import cc.landingzone.dreamweb.demo.akapply.model.policytemplate.PolicyDocument;
import cc.landingzone.dreamweb.demo.akapply.model.policytemplate.Statement;
import cc.landingzone.dreamweb.demo.akapply.model.policytemplate.StringEquals;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.tag20180828.models.ListResourcesByTagRequest;
import com.aliyun.tag20180828.models.ListResourcesByTagResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.aliyun.vpc20160428.Client;
import com.aliyun.vpc20160428.models.DescribeVSwitchAttributesRequest;
import com.aliyun.vpc20160428.models.DescribeVpcAttributeRequest;
import com.aliyun.vpc20160428.models.DescribeVpcAttributeResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceHelper {

//    private static Logger logger = LoggerFactory.getLogger(ServiceHelper.class);


    /**
     * 权限策略 拼接资源ARN
     *
     * @param resourceType:oss、log、ecs、rds、slb
     */
    public static List<String> getResourceArnInPolicy(String resourceType, List<String> resourceIdList, String accountId) {
        List<String> resourceArn = new ArrayList<>();
        switch (resourceType) {
            case "oss":
                for (String resourceName : resourceIdList) {
                    resourceArn.add("acs:oss:*:" + accountId + ":" + resourceName);
                }
                break;
            case "log":
                for (String resourceName : resourceIdList) {
                    resourceArn.add("acs:log:*:" + accountId + ":project/" + resourceName);
                }
                break;
            case "ecs":
                for (String resourceName : resourceIdList) {
                    resourceArn.add("acs:ecs:*:" + accountId + ":instance/" + resourceName);
                }
                break;
            case "rds":
                for (String resourceName : resourceIdList) {
                    resourceArn.add("acs:rds:*:" + accountId + ":instance/" + resourceName);
                }
                break;
            case "slb":
                for (String resourceName : resourceIdList) {
                    resourceArn.add("acs:slb:*:" + accountId + ":instance/" + resourceName);
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
            case "vpc":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:vpc:*:" +
                            accountId + ":vpc/" + resourceName);
                }
                break;
            case "vSwitch":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:vpc:*:" +
                            accountId + ":vswitch/" + resourceName);
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
        com.aliyun.tag20180828.Client client = ClientHelper.createTagClient
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
        com.aliyun.vpc20160428.Client client = ClientHelper.createVpcClient
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
        com.aliyun.vpc20160428.Client client = ClientHelper.createVpcClient
                (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        DescribeVpcAttributeRequest describeVpcAttributeRequest = new DescribeVpcAttributeRequest()
                .setVpcId(vpcId)
                .setRegionId("cn-hangzhou");
        RuntimeOptions runtime = new RuntimeOptions();
        return client.describeVpcAttributeWithOptions(describeVpcAttributeRequest, runtime).getBody();
    }

    /**
     * 生成权限策略内容
     *
     * @param resourceType
     * @param resourceIdList
     * @param actionCode
     * @param accountId
     * @return
     */
    public static String generatePolicyDocument(String resourceType, List<String> resourceIdList,
                                                int actionCode, String accountId) {
        PolicyDocument policyDocument = new PolicyDocument();
        List<Statement> statementList = new ArrayList<>();

        Statement statement1 = new Statement();
        List<String> resourceArn = ServiceHelper.getResourceArnInPolicy(resourceType, resourceIdList, accountId);
        List<String> action = getAction(resourceType, actionCode);
        statement1.setAction(action);
        statement1.setResource(resourceArn);
        statementList.add(statement1);

        // 日志服务的fullAccess需要额外的权限
        if ("log".equals(resourceType) && actionCode == 2) {
            Statement statement2 = new Statement();
            List<String> action2 = new ArrayList<>();
            action2.add("ram:CreateServiceLinkedRole");
            statement2.setAction(action2);
            List<String> resourceArn2 = new ArrayList<>();
            resourceArn2.add("*");
            statement2.setResource(resourceArn2);
            Condition condition = new Condition();
            StringEquals stringEquals = new StringEquals();
            stringEquals.setRamServiceName(Arrays.asList("audit.log.aliyuncs.com", "alert.log.aliyuncs.com"));
            condition.setStringEquals(stringEquals);

            statement2.setCondition(condition);

            statementList.add(statement2);
        }
        policyDocument.setStatement(statementList);
        return JSON.toJSONString(policyDocument, SerializerFeature.PrettyFormat);
    }

    /**
     * 拼接action
     *
     * @param resourceType: oss、log、ecs、rds、slb
     * @param actionCode:   1(readOnly)、2(fullAccess)、3(password-free login console)
     * @return
     */
    public static List<String> getAction(String resourceType, int actionCode) {
        List<String> action = new ArrayList<>();
        switch (actionCode) {
            case 1:
                action.add(resourceType + ":Get*");
                action.add(resourceType + ":List*");
                break;
            case 2:
                action.add(resourceType + ":*");
                break;
            case 3:
                switch (resourceType) {
                    case "oss":
                    case "log":
                        action.add(resourceType + ":Get*");
                        break;
                    case "ecs":
                    case "rds":
                    case "slb":
                        action.add(resourceType + ":Describe*");
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        return action;
    }

    /**
     * create vpc,return vpcId
     */
    public static String createVpc(String vpcName,String cidrBlock) throws Exception{
        Client vpcClient = ClientHelper.createVpcClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.vpc20160428.models.CreateVpcRequest createVpcRequest = new com.aliyun.vpc20160428.models.CreateVpcRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setVpcName(vpcName)
                .setCidrBlock(cidrBlock);
        RuntimeOptions runtime = new RuntimeOptions();
        return vpcClient.createVpcWithOptions(createVpcRequest, runtime).getBody().getVpcId();
    }

    /**
     * create vSwitch,return vSwitchId
     */
    public static String createVSwitch(String vpcId,String vSwitchName,String cidrBlock) throws Exception {
        Client vpcClient = ClientHelper.createVpcClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.vpc20160428.models.CreateVSwitchRequest createVSwitchRequest = new com.aliyun.vpc20160428.models.CreateVSwitchRequest()
                .setRegionId(CommonConstants.Aliyun_REGION_HANGZHOU)
                .setZoneId("cn-hangzhou-b")
                .setVpcId(vpcId)
                .setVSwitchName(vSwitchName)
                .setCidrBlock(cidrBlock);
        RuntimeOptions runtime = new RuntimeOptions();
        return vpcClient.createVSwitchWithOptions(createVSwitchRequest, runtime).getBody().getVSwitchId();
    }
}
