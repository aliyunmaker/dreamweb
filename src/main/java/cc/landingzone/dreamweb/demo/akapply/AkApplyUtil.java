package cc.landingzone.dreamweb.demo.akapply;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.ServiceHelper;
import cc.landingzone.dreamweb.demo.akapply.model.Condition;
import cc.landingzone.dreamweb.demo.akapply.model.PolicyDocument;
import cc.landingzone.dreamweb.demo.akapply.model.Statement;
import cc.landingzone.dreamweb.demo.akapply.model.StringEquals;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.aliyun.ram20150501.Client;
import com.aliyun.ram20150501.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AkApplyUtil {

    public static Logger logger = LoggerFactory.getLogger(AkApplyUtil.class);

    public static void main(String[] args) {

//        String ramArn = getRamArn("dreamweb",CommonConstants.Aliyun_UserId);
//        String resourceType = "oss";
////        String resourceType = "log";
//        List<String> resourceNameList = new ArrayList<>();
//        resourceNameList.add("buckttestjia");
////        resourceNameList.add("slstestjia1");
//        String policyDocument = generatePolicyDocument(resourceType,resourceNameList,
//                2,CommonConstants.Aliyun_UserId);
//
//        String applicationName = "application";
//        String environment = "test";
//        String policyName = applicationName + "-" + environment + "-" + UUID.randomUUID();
//        String username = applicationName + "-" + environment;
//        createPolicy(policyName,policyDocument);
//        createRamUser(username);
//        attachPolicyToUser(username,policyName,"Custom");
//
//        CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey accessKey = createAccessKey(username);
//        assert accessKey != null;
//        System.out.println(accessKey.accessKeyId);
//        System.out.println(accessKey.accessKeySecret);
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
        List<String> resourceArn = ServiceHelper.getResourceArnInPolicy(resourceType, resourceNameList, accountId);
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
    public static void createRamUser(String userName) throws Exception {

             Client client = ServiceHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
             RuntimeOptions runtime = new RuntimeOptions();
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
                    // 删除RAM用户的访问密钥
                    ListAccessKeysRequest listAccessKeysRequest = new ListAccessKeysRequest()
                            .setUserName(userName);
                    ListAccessKeysResponseBody.ListAccessKeysResponseBodyAccessKeys accessKeys =
                            client.listAccessKeysWithOptions(listAccessKeysRequest, runtime).getBody().getAccessKeys();
                    for (ListAccessKeysResponseBody.ListAccessKeysResponseBodyAccessKeysAccessKey accessKey : accessKeys.accessKey) {
                         DeleteAccessKeyRequest deleteAccessKeyRequest = new DeleteAccessKeyRequest()
                                .setUserName(userName)
                                .setUserAccessKeyId(accessKey.accessKeyId);
                         client.deleteAccessKeyWithOptions(deleteAccessKeyRequest, runtime);
                    }
//                    // 删除RAM用户
//                    DeleteUserRequest deleteUserRequest = new DeleteUserRequest().setUserName(userName);
//                    client.deleteUserWithOptions(deleteUserRequest, runtime);
                    return;
                }
            }
            // 创建RAM用户
            CreateUserRequest createUserRequest = new CreateUserRequest().setUserName(userName);
            client.createUserWithOptions(createUserRequest, runtime);

    }

    /**
     * 创建一个自定义权限策略
     * @param policyName：权限策略名称
     * @param policyDocument：权限策略内容
     */
    public static void createPolicy(String policyName,String policyDocument) throws Exception {

         Client client = ServiceHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
         CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                 .setPolicyName(policyName)
                 .setPolicyDocument(policyDocument);
         RuntimeOptions runtime = new RuntimeOptions();
         client.createPolicyWithOptions(createPolicyRequest, runtime);

    }



    /**
     * 为指定用户添加权限
     * @param userName
     * @param policyName
     * @param policyType：System为系统策略，Custom为自定义策略
     */
    public static void attachPolicyToUser(String userName,String policyName,String policyType) throws Exception{

             Client client = ServiceHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
             AttachPolicyToUserRequest attachPolicyToUserRequest = new AttachPolicyToUserRequest()
                     .setUserName(userName)
                     .setPolicyName(policyName)
                     .setPolicyType(policyType);
             RuntimeOptions runtime = new RuntimeOptions();
             client.attachPolicyToUserWithOptions(attachPolicyToUserRequest, runtime);

    }

    /**
     * 为RAM用户创建访问密钥
     * @param userName
     * @return
     */
    public static CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey createAccessKey(String userName)
            throws Exception{

            Client client = ServiceHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId,CommonConstants.Aliyun_AccessKeySecret);
            CreateAccessKeyRequest createAccessKeyRequest = new CreateAccessKeyRequest().setUserName(userName);
            RuntimeOptions runtime = new RuntimeOptions();
            return client.createAccessKeyWithOptions(createAccessKeyRequest, runtime)
                    .getBody().getAccessKey();

    }


}
