package cc.landingzone.dreamweb.common;

import com.aliyun.ram20150501.Client;
import com.aliyun.ram20150501.models.*;
import com.aliyun.teautil.models.RuntimeOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
public class RamHelper {

    /**
     * policyType: System or Custom
     */
    public static void attachPolicyToRole(String roleName, String policyName, String policyType) throws Exception {
        Client client = ClientHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.ram20150501.models.AttachPolicyToRoleRequest attachPolicyToRoleRequest = new com.aliyun.ram20150501.models.AttachPolicyToRoleRequest()
                .setPolicyType(policyType)
                .setPolicyName(policyName)
                .setRoleName(roleName);
        RuntimeOptions runtime = new RuntimeOptions();
        client.attachPolicyToRoleWithOptions(attachPolicyToRoleRequest, runtime);
    }

    /**
     * create ram role, return roleName
     */
    public static String createRamRole(String roleName, String assumeRolePolicyDocument) throws Exception {
        Client client = ClientHelper.createRamClient
                (CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.ram20150501.models.CreateRoleRequest createRoleRequest = new com.aliyun.ram20150501.models.CreateRoleRequest()
                .setRoleName(roleName)
                .setAssumeRolePolicyDocument(assumeRolePolicyDocument);
        RuntimeOptions runtime = new RuntimeOptions();
        CreateRoleResponseBody createRoleResponseBody = client.createRoleWithOptions(createRoleRequest, runtime).getBody();
        return createRoleResponseBody.getRole().getRoleName();
    }

    /**
     * 为RAM用户创建访问密钥
     */
    public static CreateAccessKeyResponseBody.CreateAccessKeyResponseBodyAccessKey createAccessKey(String userName)
            throws Exception {

        Client client = ClientHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        CreateAccessKeyRequest createAccessKeyRequest = new CreateAccessKeyRequest().setUserName(userName);
        RuntimeOptions runtime = new RuntimeOptions();
        return client.createAccessKeyWithOptions(createAccessKeyRequest, runtime)
                .getBody().getAccessKey();
    }

    /**
     * 创建一个自定义权限策略
     *
     * @param policyName：权限策略名称
     * @param policyDocument：权限策略内容
     */
    public static void createPolicy(String policyName, String policyDocument) throws Exception {

        Client client = ClientHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        CreatePolicyRequest createPolicyRequest = new CreatePolicyRequest()
                .setPolicyName(policyName)
                .setPolicyDocument(policyDocument);
        RuntimeOptions runtime = new RuntimeOptions();
        client.createPolicyWithOptions(createPolicyRequest, runtime);
    }

    /**
     * 为指定用户添加权限
     * @param policyType：System为系统策略，Custom为自定义策略
     */
    public static void attachPolicyToUser(String userName, String policyName, String policyType) throws Exception {
        Client client = ClientHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        AttachPolicyToUserRequest attachPolicyToUserRequest = new AttachPolicyToUserRequest()
                .setUserName(userName)
                .setPolicyName(policyName)
                .setPolicyType(policyType);
        RuntimeOptions runtime = new RuntimeOptions();
        client.attachPolicyToUserWithOptions(attachPolicyToUserRequest, runtime);
    }

    /**
     * get all roles
     */
    public static List<String> listRoles() throws Exception {
        Client client = ClientHelper.createRamClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        ListRolesRequest listRolesRequest = new ListRolesRequest();
        RuntimeOptions runtime = new RuntimeOptions();
        List<String> roleList = new ArrayList<>();
        for (ListRolesResponseBody.ListRolesResponseBodyRolesRole listRolesResponseBodyRolesRole : client.listRolesWithOptions(listRolesRequest, runtime).getBody().getRoles().getRole()) {
            roleList.add(listRolesResponseBodyRolesRole.getRoleName());
        }
        return roleList;
    }

}
