package cc.landingzone.dreamweb.sso.sp;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.EndpointEnum;
import cc.landingzone.dreamweb.utils.UUIDUtils;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import io.jsonwebtoken.lang.Assert;

public class RAMSamlHelper {

    public static void main(String[] args) throws Exception {
        String stsEndpoint = EndpointEnum.STS.getEndpoint();
        DefaultProfile profile = DefaultProfile.getProfile(
                CommonConstants.Aliyun_REGION_HANGZHOU,
                CommonConstants.Aliyun_AccessKeyId,
                CommonConstants.Aliyun_AccessKeySecret);
        String result = querySAMLToken(profile, "acs:ram::1764263140474643:saml-provider/superAD",
                "acs:ram::1764263140474643:role/super3", null, stsEndpoint);
        System.out.println(result);
    }

    public static String querySAMLToken(DefaultProfile profile, String samlProviderArn, String roleArn,
                                        String samlAssertion, String endpoint) throws Exception {
        Assert.notNull(profile, "profile can not be null!");
        Assert.hasText(samlProviderArn, "samlProviderArn can not be blank!");
        Assert.hasText(roleArn, "roleArn can not be blank!");
        Assert.hasText(samlAssertion, "samlAssertion can not be blank!");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setSysDomain(endpoint);
        request.setSysVersion("2015-04-01");
        request.setSysAction("AssumeRoleWithSAML");
        request.setSysProtocol(ProtocolType.HTTPS);
        request.putQueryParameter("SAMLProviderArn", samlProviderArn);
        request.putQueryParameter("RoleArn", roleArn);
        request.putQueryParameter("Timestamp", String.valueOf(System.currentTimeMillis()));
        request.putQueryParameter("SignatureNonce", UUIDUtils.generateUUID());
        request.putQueryParameter("SAMLAssertion", samlAssertion);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }

}
