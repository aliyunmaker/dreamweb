package cc.landingzone.dreamweb.demo.akapply;

import cc.landingzone.dreamweb.common.ClientHelper;
import cc.landingzone.dreamweb.common.CommonConstants;
import com.aliyun.ram20150501.Client;
import com.aliyun.ram20150501.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 创建RAM用户
     *
     * @param userName
     */
    public static void createRamUser(String userName) throws Exception {

        Client client = ClientHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
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
                    if (attachmentCount == 0) {
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


}
