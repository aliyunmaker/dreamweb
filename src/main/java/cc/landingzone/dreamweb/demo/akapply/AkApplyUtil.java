package cc.landingzone.dreamweb.demo.akapply;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.demo.akapply.model.Condition;
import cc.landingzone.dreamweb.demo.akapply.model.PolicyDocument;
import cc.landingzone.dreamweb.demo.akapply.model.Statement;
import cc.landingzone.dreamweb.demo.akapply.model.StringEquals;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.ram20150501.Client;
import com.aliyun.ram20150501.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class AkApplyUtil {

    public static Logger logger = LoggerFactory.getLogger(AkApplyUtil.class);

    public static void main(String[] args) {
        String ramArn = getRamArn("dreamweb",CommonConstants.Aliyun_UserId);
//        String resourceType = "oss";
        String resourceType = "log";
        List<String> resourceNameList = new ArrayList<>();
//        resourceNameList.add("buckttest11");
        resourceNameList.add("slstestjia1");
        String policyDocument = generatePolicyDocument(resourceType,resourceNameList,
                2,CommonConstants.Aliyun_UserId);

        String applicationName = "application";
        String environment = "test";
        String policyName = applicationName + "-" + environment + "-" + UUID.randomUUID();
        String username = applicationName + "-" + environment;
        createPolicy(policyName,policyDocument);
        createRamUser(username);
        attachPolicyToUser(username,policyName,"Custom");

        CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey accessKey = createAccessKey(username);
        assert accessKey != null;
        System.out.println(accessKey.accessKeyId);
        System.out.println(accessKey.accessKeySecret);
    }

    /**
     * 拼接资源ARN
     * @param resourceType:oss、log
     * @param resourceNameList
     * @return
     */
    public static List<String> getResourceArn(String resourceType, List<String> resourceNameList,String accountId){
        List<String> resourceArn = new ArrayList<>();
        switch (resourceType){
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
     * 拼接action
     * @param resourceType: oss、log
     * @param actionCode: 1(readOnly)、2(fullAccess)
     * @return
     */
    public static List<String> getAction(String resourceType,int actionCode){
        List<String> action = new ArrayList<>();
        switch (actionCode){
            case 1:
                action.add(resourceType + ":Get*");
                action.add(resourceType + ":List*");
                break;
            case 2:
                action.add(resourceType + ":*");
                break;
            default:
                break;
        }
        return action;
    }

    /**
     * 拼接RAM用户的ARN：acs:ram::<account-id>:user/<user-name>
     * @param userName
     * @param accountId
     * @return
     */
    public static String getRamArn(String userName,String accountId){
        return "acs:ram::" + accountId + ":user/" + userName;
    }

    /**
     * 生成权限策略内容
     * @param resourceType
     * @param resourceNameList
     * @param actionCode
     * @param accountId
     * @return
     */
    public static String generatePolicyDocument(String resourceType, List<String> resourceNameList,
                                                int actionCode,String accountId){
        PolicyDocument policyDocument = new PolicyDocument();
        List<Statement> statementList = new ArrayList<>();

        Statement statement1 = new Statement();
        List<String> resourceArn = getResourceArn(resourceType, resourceNameList,accountId);
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
     * 创建RAM用户
     * @param userName
     */
    public static void createRamUser(String userName){
        Client client = AkApplyUtil.createClientRam(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            assert client != null;
            // 查询所有RAM用户
            ListUsersRequest listUsersRequest = new ListUsersRequest();
            ListUsersResponseBody.ListUsersResponseBodyUsers users = client.listUsersWithOptions(listUsersRequest,
                    runtime).getBody().getUsers();
            for (ListUsersResponseBody.ListUsersResponseBodyUsersUser user : users.user) {
                // 用户已存在
                if (userName.equals(user.getUserName())) {
                    // 查询用户是否有自定义权限策略
                    ListPoliciesForUserRequest listPoliciesForUserRequest = new ListPoliciesForUserRequest()
                            .setUserName(userName);
                    ListPoliciesForUserResponseBody.ListPoliciesForUserResponseBodyPolicies policies =
                            client.listPoliciesForUserWithOptions(listPoliciesForUserRequest, runtime).getBody().getPolicies();
                    for (ListPoliciesForUserResponseBody.ListPoliciesForUserResponseBodyPoliciesPolicy policy : policies.policy) {
                        // 如果有权限策略
                        // 为用户撤销该权限策略
                        DetachPolicyFromUserRequest detachPolicyFromUserRequest = new DetachPolicyFromUserRequest()
                                .setPolicyType(policy.policyType)
                                .setPolicyName(policy.policyName)
                                .setUserName(userName);
                        client.detachPolicyFromUserWithOptions(detachPolicyFromUserRequest, runtime);
                        // 查询权限策略的引用次数
                        GetPolicyRequest getPolicyRequest = new GetPolicyRequest()
                                .setPolicyType(policy.policyType)
                                .setPolicyName(policy.policyName);
                        Integer attachmentCount = client.getPolicyWithOptions(getPolicyRequest, runtime)
                                .getBody().getPolicy().getAttachmentCount();
                        // 引用次数为0，删除权限策略
                        if (attachmentCount == 0){
                            // 删除权限策略
                            DeletePolicyRequest deletePolicyRequest = new com.aliyun.ram20150501.models.DeletePolicyRequest()
                                    .setPolicyName(policy.getPolicyName());
                            client.deletePolicyWithOptions(deletePolicyRequest, runtime);
                        }
                    }
                    // 删除RAM用户
                    DeleteUserRequest deleteUserRequest = new DeleteUserRequest().setUserName(userName);
                    client.deleteUserWithOptions(deleteUserRequest, runtime);
                }
            }
            // 创建RAM用户
            CreateUserRequest createUserRequest = new CreateUserRequest().setUserName(userName);
            client.createUserWithOptions(createUserRequest, runtime);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 创建一个自定义权限策略
     * @param policyName：权限策略名称
     * @param policyDocument：权限策略内容
     */
    public static void createPolicy(String policyName,String policyDocument)  {
        Client client = AkApplyUtil.createClientRam(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
        CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                .setPolicyName(policyName)
                .setPolicyDocument(policyDocument);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            assert client != null;
            client.createPolicyWithOptions(createPolicyRequest, runtime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }



    /**
     * 为指定用户添加权限
     * @param userName
     * @param policyName
     * @param policyType：System为系统策略，Custom为自定义策略
     */
    public static void attachPolicyToUser(String userName,String policyName,String policyType) {
        Client client = AkApplyUtil.createClientRam(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
        AttachPolicyToUserRequest attachPolicyToUserRequest = new AttachPolicyToUserRequest()
                .setUserName(userName)
                .setPolicyName(policyName)
                .setPolicyType(policyType);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            assert client != null;
            client.attachPolicyToUserWithOptions(attachPolicyToUserRequest, runtime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 为RAM用户创建访问密钥
     * @param userName
     * @return
     */
    public static CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey createAccessKey(String userName){
        Client client = AkApplyUtil.createClientRam(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
        CreateAccessKeyRequest createAccessKeyRequest = new CreateAccessKeyRequest().setUserName(userName);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            assert client != null;
            CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey accessKey =
                    client.createAccessKeyWithOptions(createAccessKeyRequest, runtime).getBody().getAccessKey();
            return accessKey;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     */
    public static Client createClientRam(String accessKeyId, String accessKeySecret) {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "ram.aliyuncs.com";
        try {
            return new Client(config);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }


}
