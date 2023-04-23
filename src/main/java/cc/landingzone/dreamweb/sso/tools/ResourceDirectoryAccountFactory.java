package cc.landingzone.dreamweb.sso.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToRoleRequest;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToRoleResponse;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityRequest;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityResponse;

import cc.landingzone.dreamweb.sso.SamlGenerator;
import cc.landingzone.dreamweb.utils.UUIDUtils;

/**
 * 利用已认证主体的账号生成新账号,并且开通RD,成为新RD的Master,并把原来账号邀请进来
 */

@Component
public class ResourceDirectoryAccountFactory {

    public static final ThreadLocal<String> ctx = new ThreadLocal<>();

    public static final Logger logger = LoggerFactory.getLogger(ResourceDirectoryAccountFactory.class);

    public static void loggerInfo(String msg) {
        logger.info(msg);
        String traceId = ctx.get();
        if (null == traceId) {
            return;
        }
    }

    public static void buildNewRD(String masterAccountAccessKeyId, String masterAccountAccessKeySecret, String email, String traceId) {
        ctx.set(traceId);
        try {
            DefaultProfile AliyunProfile = DefaultProfile.getProfile("cn-hangzhou");
            IAcsClient masterClient = new DefaultAcsClient(AliyunProfile, new BasicCredentials(masterAccountAccessKeyId, masterAccountAccessKeySecret));
            String callerIdentity = GetCallerIdentity(masterClient);
            String masterAccountId = JSON.parseObject(callerIdentity).getString("AccountId");
            String masterIdentityType = JSON.parseObject(callerIdentity).getString("IdentityType");
            //0. 打印当前AK账号的信息
            loggerInfo("*******************************************************************");
            loggerInfo("当前账号信息:");
            loggerInfo("AccountId: " + masterAccountId);
            loggerInfo("IdentityType: " + masterIdentityType);
            loggerInfo("Arn: " + JSON.parseObject(callerIdentity).getString("Arn"));
            loggerInfo("*******************************************************************");

            //1. 初始化RD
            loggerInfo("1. 初始化RD");
            loggerInfo("*******************************************************************");
            String rdInfo = GetResourceDirectory(masterClient);
            if (null != rdInfo) {
                logger.info("rdInfo:" + rdInfo);
                //有RD信息有两种情况:
                //a: 自己是RD的Master
                //b: 自己是RD的sub account
                String rdMasterAccountId = JSON.parseObject(rdInfo).getJSONObject("ResourceDirectory").getString("MasterAccountId");
                if (masterAccountId.equals(rdMasterAccountId)) {
                    int count = ListAccounts(masterClient);
                    if (count > 0) {
                        loggerInfo("this account has sub account,rdinfo:" + rdInfo);
                        loggerInfo("__done__");
                        return;
                    }
                } else {
                    loggerInfo("this account is a sub account in existed RD,rdinfo:" + rdInfo);
                    loggerInfo("__done__");
                    return;
                }
            } else {
                //如果rdInfo为null,则表示该账号没有开通过RD
                initResourceDirectory(masterClient);
            }


            //2. 创建成员账号,继承主体认证
            loggerInfo("2. 创建成员账号,继承主体认证");
            loggerInfo("*******************************************************************");
            rdInfo = GetResourceDirectory(masterClient);
            String rootFolderId = JSON.parseObject(rdInfo).getJSONObject("ResourceDirectory").getString("RootFolderId");
            String createResult = CreateResourceAccount(masterClient, "langingzone_" + UUIDUtils.generateUUID(), rootFolderId);
            String accountId = JSON.parseObject(createResult).getJSONObject("Account").getString("AccountId");
            PromoteResourceAccount(masterClient, accountId, email);


            boolean confirmEmail = false;
            //3. 等待用户点击"立即确认"按钮
            loggerInfo("3. 等待用户点击\"立即确认\"按钮");
            loggerInfo("*******************************************************************");
            for (int i = 0; i < 60; i++) {
                Thread.sleep(10000);
                String accountInfo = GetAccount(masterClient, accountId);
                String accountType = JSON.parseObject(accountInfo).getJSONObject("Account").getString("Type");
                if ("CloudAccount".equals(accountType)) {
                    confirmEmail = true;
                    break;
                }
                loggerInfo("waiting confirm email,accountId:" + accountId);
            }

            if (!confirmEmail) {
                loggerInfo("reach max waiting count,exit!");
                return;
            }

            //4. 获取新账号的ram user的AK
            loggerInfo("4. 获取新账号的ram user的AK");
            loggerInfo("*******************************************************************");
            //4.1 先换出master账号的ram user 的AK
            String assumeRoleAccessKeyId = masterAccountAccessKeyId;
            String assumeRoleAccessKeySecret = masterAccountAccessKeySecret;
            String masterRamUserName = "";
            if ("Account".equals(masterIdentityType)) {
                loggerInfo("4.1 先换出master账号的ram user 的AK");
                masterRamUserName = "landingzone_master_" + UUIDUtils.generateUUID();
                Map<String, String> masterRamUserAKMap = CreateRamUserAccessKey(masterClient, masterAccountId,
                    masterRamUserName);
                assumeRoleAccessKeyId = masterRamUserAKMap.get("AccessKeyId");
                assumeRoleAccessKeySecret = masterRamUserAKMap.get("AccessKeySecret");
            } else {
                loggerInfo("4.1 当前账号满足需求，无须创建子账号");
            }
            STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(new BasicCredentials(assumeRoleAccessKeyId, assumeRoleAccessKeySecret), "acs:ram::" + accountId + ":role/resourcedirectoryaccountaccessrole", AliyunProfile);
            DefaultAcsClient assumeRoleClient = new DefaultAcsClient(AliyunProfile, provider);

            //4.2 换取目标账户ram user的AK
            loggerInfo("4.2 换取目标账户ram user的AK");
            String subRamUserName = "masteradmin";
            Map<String, String> subRamUserAKMap = CreateRamUserAccessKey(assumeRoleClient, accountId, subRamUserName);
            String subRamUserPassword = RandomStringUtils.randomAlphanumeric(10);
            CreateLoginProfile(assumeRoleClient, subRamUserName, subRamUserPassword);
            IAcsClient newAccountRamUserClient = new DefaultAcsClient(AliyunProfile, new BasicCredentials(subRamUserAKMap.get("AccessKeyId"), subRamUserAKMap.get("AccessKeySecret")));

            if (!StringUtils.isEmpty(masterRamUserName)) {
                //4.3 清理主账号的ram user
                loggerInfo("4.3 清理主账号的ram user");
                DeleteUser(masterClient, masterRamUserName, assumeRoleAccessKeyId, true);
            }

            //5. 解绑账号
            loggerInfo("5. 解绑账号");
            loggerInfo("*******************************************************************");
            RemoveCloudAccount(masterClient, accountId);

            //6. 新创建的账号开通RD
            loggerInfo("6. 新创建的账号开通RD");
            loggerInfo("*******************************************************************");
            initResourceDirectory(newAccountRamUserClient);

            //7. 关闭原来的账号RD
            loggerInfo("7. 关闭原来的账号RD");
            loggerInfo("*******************************************************************");
            DestroyResourceDirectory(masterClient);

            //8. 用新账号邀请原来账号进入RD
            loggerInfo("8. 用新账号邀请原来账号进入RD");
            loggerInfo("*******************************************************************");
            String inviteResult = InviteAccountToResourceDirectory(newAccountRamUserClient, masterAccountId);
            String handshakeId = JSON.parseObject(inviteResult).getJSONObject("Handshake").getString("HandshakeId");
            Thread.sleep(3000);
            AcceptHandshake(masterClient, handshakeId);

            //8.1 标准化新账号RD的目录结构
            loggerInfo("8.1 标准化新账号RD的目录结构");
            CreateFolder(newAccountRamUserClient, "Core");
            String createFolderResult = CreateFolder(newAccountRamUserClient, "Applications");
            String applicationsFolderId = JSON.parseObject(createFolderResult).getJSONObject("Folder").getString("FolderId");
            //8.2 将邀请的账号move到新文件夹
            loggerInfo("8.2 将邀请的账号move到新文件夹");
            MoveAccount(newAccountRamUserClient, masterAccountId, applicationsFolderId);


            //9. 配置账号的role base sso
//            loggerInfo("9. 配置账号的role base sso");
//            loggerInfo("*******************************************************************");
//            String roleExpression = initSP(newAccountRamUserClient, "chengchaoIDP", "mycloudadmin");
//            String ssoRandomSuffix = RandomStringUtils.randomAlphanumeric(5);
//            String subAccountRoleExpression = initSP(masterClient, "chengchaoIDP" + ssoRandomSuffix, "mycloudadmin" + ssoRandomSuffix);

            //9. 清理新账号的ram user,由于循环问题不删除user,只清理改user的policy 和 ak
            loggerInfo("9. 清理新账号的ram user的policy和ak");
            loggerInfo("*******************************************************************");
            DeleteUser(newAccountRamUserClient, subRamUserName, subRamUserAKMap.get("AccessKeyId"), false);
            loggerInfo("**************************************************************************************************************************************");
            loggerInfo("master account: " + email);
            loggerInfo("accountId: " + accountId);
            loggerInfo("请通过邮箱找回密码的方式登录新创建的主账号");
            loggerInfo("-------------------------------------------------------------------");
            loggerInfo("ram user name: " + subRamUserName);
            loggerInfo("ram user password: " + subRamUserPassword);
            loggerInfo("ram user login url: ");
            loggerInfo("https://signin.aliyun.com/" + accountId + ".onaliyun.com/login.htm?callback=https%3A%2F%2Fhomenew.console.aliyun.com%2F#/login");
//            loggerInfo("-------------------------------------------------------------------");
//            loggerInfo("sso roleExpression(master):" + accountId);
//            loggerInfo(roleExpression);
//            loggerInfo("sso roleExpression(sub):" + masterAccountId);
//            loggerInfo(subAccountRoleExpression);
            loggerInfo("**************************************************************************************************************************************");
            loggerInfo("__done__");
        } catch (Exception e) {
            loggerInfo(e.getMessage());
        }
    }

    public static Map<String, String> CreateRamUserAccessKey(IAcsClient client, String accountId, String ramUserName) throws Exception {
        CommonRequest request = new CommonRequest();
        CommonResponse response;

        //CreateUser
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("CreateUser");
        request.putQueryParameter("UserName", ramUserName);
        request.setSysProtocol(ProtocolType.HTTPS);
        response = client.getCommonResponse(request);
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());

        //AttachPolicyToUser
        request = new CommonRequest();
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("AttachPolicyToUser");
        request.putQueryParameter("PolicyType", "System");
        request.putQueryParameter("PolicyName", "AdministratorAccess");
        request.putQueryParameter("UserName", ramUserName);
        request.setSysProtocol(ProtocolType.HTTPS);
        response = client.getCommonResponse(request);
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());


        //CreateAccessKey
        request = new CommonRequest();
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("CreateAccessKey");
        request.putQueryParameter("UserName", ramUserName);
        request.setSysProtocol(ProtocolType.HTTPS);
        response = client.getCommonResponse(request);
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());

        String AccessKeyId = JSON.parseObject(response.getData()).getJSONObject("AccessKey").getString("AccessKeyId");
        String AccessKeySecret = JSON.parseObject(response.getData()).getJSONObject("AccessKey").getString("AccessKeySecret");


        Map<String, String> akMap = new HashMap<>();
        akMap.put("AccessKeyId", AccessKeyId);
        akMap.put("AccessKeySecret", AccessKeySecret);
        return akMap;

    }


    public static void CreateLoginProfile(IAcsClient client, String userName, String password) throws Exception {
        CommonRequest request = new CommonRequest();
        CommonResponse response;

        //DeleteAccessKey
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("CreateLoginProfile");
        request.putQueryParameter("UserName", userName);
        request.putQueryParameter("Password", password);
        request.putQueryParameter("PasswordResetRequired", "true");

        request.setSysProtocol(ProtocolType.HTTPS);
        response = client.getCommonResponse(request);
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());
    }

    public static void DeleteUser(IAcsClient client, String userName, String userAccessKeyId, boolean deleteUser) throws Exception {
        CommonRequest request = new CommonRequest();
        CommonResponse response;

        //DeleteAccessKey
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("DeleteAccessKey");
        request.putQueryParameter("UserAccessKeyId", userAccessKeyId);
        request.putQueryParameter("UserName", userName);
        request.setSysProtocol(ProtocolType.HTTPS);
        response = client.getCommonResponse(request);
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());

        if (deleteUser) {
            //DetachPolicyFromUser
            request.setSysDomain("ram.aliyuncs.com");
            request.setSysVersion("2015-05-01");
            request.setSysAction("DetachPolicyFromUser");
            request.putQueryParameter("PolicyType", "System");
            request.putQueryParameter("PolicyName", "AdministratorAccess");
            request.putQueryParameter("UserName", userName);
            request.setSysProtocol(ProtocolType.HTTPS);
            response = client.getCommonResponse(request);
            loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());

            //DeleteUser
            request.setSysDomain("ram.aliyuncs.com");
            request.setSysVersion("2015-05-01");
            request.setSysAction("DeleteUser");
            request.putQueryParameter("UserName", userName);
            request.setSysProtocol(ProtocolType.HTTPS);
            response = client.getCommonResponse(request);
            loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + response.getData());
        }
    }


    public static String CreateResourceAccount(IAcsClient client, String displayName, String parentFolderId) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("CreateResourceAccount");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("DisplayName", displayName);
        request.putQueryParameter("ParentFolderId", parentFolderId);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }


    /**
     * 升级成云账号,会发送邮件,需要在邮件中"立即确认"
     *
     * @param accountId
     * @param email
     * @throws Exception
     */
    public static String PromoteResourceAccount(IAcsClient client, String accountId, String email) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("PromoteResourceAccount");
        request.putQueryParameter("AccountId", accountId);
        request.putQueryParameter("Email", email);
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;

    }


    /**
     * 把云账号从RD中解绑
     *
     * @param accountId
     * @throws Exception
     */
    public static String RemoveCloudAccount(IAcsClient client, String accountId) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("RemoveCloudAccount");
        request.putQueryParameter("AccountId", accountId);
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;

    }

    /**
     * 开通RD
     *
     * @throws Exception
     */
    public static void initResourceDirectory(IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("InitResourceDirectory");
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
    }

    public static String GetResourceDirectory(IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("GetResourceDirectory");
        request.setSysProtocol(ProtocolType.HTTPS);
        try {
            CommonResponse response = client.getCommonResponse(request);
            String result = response.getData();
            loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
            return result;
        } catch (ClientException clientException) {
            if ("ResourceDirectoryNotInUse".equals(clientException.getErrCode())) {
                return null;
            }
            throw clientException;
        }
    }

    public static int ListAccounts(IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("ListAccounts");
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return JSON.parseObject(result).getInteger("TotalCount");
    }


    public static String GetAccount(IAcsClient client, String accountId) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("GetAccount");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("AccountId", accountId);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }

    public static void DestroyResourceDirectory(IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("DestroyResourceDirectory");
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
    }


    public static String InviteAccountToResourceDirectory(IAcsClient client, String accountId) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("InviteAccountToResourceDirectory");
        request.putQueryParameter("TargetType", "Account");
        request.putQueryParameter("TargetEntity", accountId);
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }


    public static String AcceptHandshake(IAcsClient client, String handshakeId) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("AcceptHandshake");
        request.putQueryParameter("HandshakeId", handshakeId);
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }

    public static String CreateFolder(IAcsClient client, String folderName) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("CreateFolder");
        request.putQueryParameter("FolderName", folderName);
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }

    public static String MoveAccount(IAcsClient client, String accountId, String destinationFolderId) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("resourcemanager.aliyuncs.com");
        request.setSysVersion("2020-03-31");
        request.setSysAction("MoveAccount");
        request.putQueryParameter("AccountId", accountId);
        request.putQueryParameter("DestinationFolderId", destinationFolderId);
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }

    public static String GetCallerIdentity(IAcsClient client) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("sts.aliyuncs.com");
        request.setSysVersion("2015-04-01");
        request.setSysAction("GetCallerIdentity");
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }


    //**********************************************sso idp配置相关**********************************************************

    public static String initSP(IAcsClient client, String idpProviderName, String roleName) throws Exception {
        String uid = getUid(client);


        loggerInfo("9. 配置新账号的role base sso");
        loggerInfo("*******************************************************************");
        loggerInfo("UID:" + uid);
        loggerInfo("idpProvider: " + idpProviderName);
        loggerInfo("roleName: " + roleName);

        String policyDocument = "{\"Statement\":[{\"Action\":\"sts:AssumeRole\",\"Condition\":{\"StringEquals\":{\"saml:recipient\":\"https://signin.aliyun.com/saml-role/sso\"}},\"Effect\":\"Allow\",\"Principal\":{\"Federated\":[\"acs:ram::"
                + uid + ":saml-provider/" + idpProviderName + "\"]}}],\"Version\":\"1\"}";
        String policyName = "AdministratorAccess";
        String policyType = "System";

        loggerInfo("policyDocument: " + policyDocument);
        loggerInfo("policyName: " + policyName);
        loggerInfo("policyType: " + policyType);

        loggerInfo("9.1 add saml provider");
        // 1. add saml provider, this samlMetadata can download from IDP(for example: Azure AD)
        String samlMetadata = SamlGenerator.generateMetaXML();
        String addSAMLProviderResult = addSAMLProviders(client, idpProviderName, samlMetadata);
        loggerInfo("result: " + addSAMLProviderResult);

        loggerInfo("9.2 create role");
        // 2. create role
        String createRoleResult = createRole(client, roleName, policyDocument);
        loggerInfo("result: " + createRoleResult);

        loggerInfo("9.3 attach policy to role");
        // 3. attach policy to role
        String attachPolicyToRoleResult = attachPolicyToRole(client, policyName, policyType, roleName);
        loggerInfo("result: " + attachPolicyToRoleResult);

        String roleExpression = "acs:ram::" + uid + ":role/" + roleName + ",acs:ram::" + uid + ":saml-provider/"
                + idpProviderName;
        loggerInfo("roleExpression: " + roleExpression);
        return roleExpression;
    }


    public static String attachPolicyToRole(IAcsClient client, String policyName, String policyType,
                                            String roleName) throws Exception {
        AttachPolicyToRoleRequest request = new AttachPolicyToRoleRequest();
        request.setPolicyName(policyName);
        request.setPolicyType(policyType);
        request.setRoleName(roleName);
        AttachPolicyToRoleResponse response = client.getAcsResponse(request);
        return response.getRequestId();
    }

    public static String createRole(IAcsClient client, String roleName, String policyDocument) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("ram.aliyuncs.com");
        request.setSysVersion("2015-05-01");
        request.setSysAction("CreateRole");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("RoleName", roleName);
        request.putQueryParameter("AssumeRolePolicyDocument", policyDocument);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }

    public static String getUid(IAcsClient client) throws Exception {
        GetCallerIdentityRequest request = new GetCallerIdentityRequest();
        GetCallerIdentityResponse response = client.getAcsResponse(request);
        return response.getAccountId();
    }

    public static String addSAMLProviders(IAcsClient client, String providerName, String samlMetadata)
            throws Exception {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("ims.aliyuncs.com");
        request.setSysVersion("2019-08-15");
        request.setSysAction("CreateSAMLProvider");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("SAMLProviderName", providerName);
        request.putQueryParameter("SAMLMetadataDocument", samlMetadata);
        CommonResponse response = client.getCommonResponse(request);
        String result = response.getData();
        loggerInfo("[" + request.getSysDomain() + "][" + request.getSysAction() + "]:" + result);
        return result;
    }


}
