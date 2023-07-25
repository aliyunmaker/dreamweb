package cc.landingzone.dreamcmp.demo.akapply;

import com.aliyun.ram20150501.Client;
import com.aliyun.ram20150501.models.*;
import com.aliyun.teautil.models.RuntimeOptions;

import cc.landingzone.dreamcmp.common.ClientHelper;
import cc.landingzone.dreamcmp.common.CommonConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AkApplyUtil {

    public static Logger logger = LoggerFactory.getLogger(AkApplyUtil.class);

    public static void main(String[] args) {
        String ramUserName = "test";
        String filters = "[{\"Key\":\"SecretName\", \"Values\":[\"" + ramUserName + "\"]}]";
        System.out.println(filters);
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

    /**
     * if secret exist，delete
     * then create a new secret
     */
    public static String createSecretByExist(String applicationName,String environment,String ramUserName,
                                      String accessKeyId,String accessKeySecret) throws Exception {
//        String filters = "[{\"Key\":\"SecretName\", \"Values\":[\"" + ramUserName + "\"]},"
//                + "{\"Key\":\"DKMSInstanceId\", \"Values\":[\"" + CommonConstants.DKMSInstanceId + "\"]}]";
        String filters = "[{\"Key\":\"SecretName\", \"Values\":[\"" + ramUserName + "\"]}]";
        List<String> listSecrets = KMSHelper.listSecrets(filters);
        if (listSecrets.size() > 0) {
            KMSHelper.deleteSecret(listSecrets.get(0));
        }
        return KMSHelper.createSecret(applicationName,environment,ramUserName,accessKeyId,accessKeySecret);
    }

}
