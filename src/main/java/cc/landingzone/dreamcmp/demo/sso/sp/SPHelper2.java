package cc.landingzone.dreamcmp.demo.sso.sp;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToRoleRequest;
import com.aliyuncs.ram.model.v20150501.AttachPolicyToRoleResponse;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityRequest;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityResponse;

import cc.landingzone.dreamcmp.common.EndpointEnum;

public class SPHelper2 {

    private static String LINE_BREAK = "\n";
    private static String LINE_SEPARATED = "--------------------------------------";

    public static void main(String[] args) throws Exception {
        String idpProviderName = "MyAzureAD";
        String roleName = "MyAdmin";
        String region = "cn-hangzhou";
        String accessKeyId = "<your accessKeyId>";
        String accessKeySecret = "<your accessKeySecret>";
        String samlMetadata = "<download from IDP,for example: Azure AD>";
        DefaultProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
        String result = initSP(profile, idpProviderName, roleName, samlMetadata);
        System.out.println(result);

    }

    /**
     * init sp (alicloud)
     *
     * @param profile
     * @param idpProviderName
     * @param roleName
     * @param samlMetadata
     * @return
     * @throws Exception
     */
    public static String initSP(DefaultProfile profile, String idpProviderName, String roleName, String samlMetadata) throws Exception {
        StringBuilder result = new StringBuilder();

        String stsEndpoint = EndpointEnum.STS.getEndpoint();
        String imsEndpoint = EndpointEnum.IMS.getEndpoint();
        String ramEndpoint = EndpointEnum.RAM.getEndpoint();

        String uid = getUid(profile, stsEndpoint);

        result.append("UID:" + uid);
        result.append(LINE_BREAK);
        result.append("idpProvider: " + idpProviderName);
        result.append(LINE_BREAK);
        result.append("roleName: " + roleName);
        result.append(LINE_BREAK);

        String policyDocument = "{\"Statement\":[{\"Action\":\"sts:AssumeRole\",\"Condition\":{\"StringEquals\":{\"saml:recipient\":\"https://signin.aliyun.com/saml-role/sso\"}},\"Effect\":\"Allow\",\"Principal\":{\"Federated\":[\"acs:ram::"
                + uid + ":saml-provider/" + idpProviderName + "\"]}}],\"Version\":\"1\"}";
        // for example: attach build-in policy "AdministratorAccess" to the role
        String policyName = "AdministratorAccess";
        String policyType = "System";

        result.append("policyDocument: " + policyDocument);
        result.append(LINE_BREAK);
        result.append("policyName: " + policyName);
        result.append(LINE_BREAK);
        result.append("policyType: " + policyType);
        result.append(LINE_BREAK);
        result.append(LINE_BREAK);
        result.append(LINE_SEPARATED);

        result.append("1. add saml provider");
        result.append(LINE_BREAK);
        // 1. add saml provider
        String addSAMLProviderResult = addSAMLProviders(profile, idpProviderName, samlMetadata, imsEndpoint);
        result.append("result: " + addSAMLProviderResult);
        result.append(LINE_BREAK);
        result.append(LINE_BREAK);
        result.append(LINE_SEPARATED);

        result.append("2. create role");
        result.append(LINE_BREAK);
        // 2. create role
        String createRoleResult = createRole(profile, roleName, policyDocument, ramEndpoint);
        result.append("result: " + createRoleResult);
        result.append(LINE_BREAK);
        result.append(LINE_BREAK);
        result.append(LINE_SEPARATED);

        result.append("3. attach policy to role");
        result.append(LINE_BREAK);
        // 3. attach policy to role
        String attachPolicyToRoleResult = attachPolicyToRole(profile, policyName, policyType, roleName, ramEndpoint);
        result.append("result: " + attachPolicyToRoleResult);
        result.append(LINE_BREAK);
        result.append(LINE_BREAK);
        result.append(LINE_SEPARATED);

        String roleExpression = "acs:ram::" + uid + ":role/" + roleName + ",acs:ram::" + uid + ":saml-provider/"
                + idpProviderName;
        result.append("roleExpression: " + roleExpression);
        return result.toString();
    }

    public static String attachPolicyToRole(DefaultProfile profile, String policyName, String policyType,
                                            String roleName, String endpoint) throws Exception {
        AttachPolicyToRoleRequest request = new AttachPolicyToRoleRequest();
        request.setSysEndpoint(endpoint);
        request.setPolicyName(policyName);
        request.setPolicyType(policyType);
        request.setRoleName(roleName);
        IAcsClient client = new DefaultAcsClient(profile);
        AttachPolicyToRoleResponse response = client.getAcsResponse(request);
        return response.getRequestId();
    }

    public static String createRole(DefaultProfile profile, String roleName, String policyDocument, String endpoint) throws Exception {
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysDomain(endpoint);
        request.setSysVersion("2015-05-01");
        request.setSysAction("CreateRole");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("RoleName", roleName);
        request.putQueryParameter("AssumeRolePolicyDocument", policyDocument);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }

    public static String getUid(DefaultProfile profile, String endpoint) throws Exception {
        GetCallerIdentityRequest request = new GetCallerIdentityRequest();
        request.setSysEndpoint(endpoint);

        IAcsClient client = new DefaultAcsClient(profile);
        GetCallerIdentityResponse response = client.getAcsResponse(request);
        return response.getAccountId();
    }

    public static String addSAMLProviders(DefaultProfile profile, String providerName, String samlMetadata, String endpoint)
            throws Exception {
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysDomain(endpoint);
        request.setSysVersion("2019-08-15");
        request.setSysAction("CreateSAMLProvider");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("SAMLProviderName", providerName);
        request.putQueryParameter("SAMLMetadataDocument", samlMetadata);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }

}
