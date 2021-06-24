package cc.landingzone.dreamweb.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SlsUtils {

    private static Logger logger = LoggerFactory.getLogger(SlsUtils.class);

    private static final String STS_HOST = "sts.aliyuncs.com";
    private static final String SIGN_IN_HOST = "https://signin.aliyun.com";

    /**
     * 访问令牌服务获取临时AK和Token
     *
     * @return 临时AK和Token
     * @throws ClientException
     */
    public static CommonResponse requestAccessKeyAndSecurityToken(String region, String roleArn, String samlProviderArn,
                                                                  String samlAssertion) throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(region, "", "");

        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysProtocol(ProtocolType.HTTPS);
        request.setSysDomain(STS_HOST);
        request.setSysVersion("2015-04-01");
        request.setSysAction("AssumeRoleWithSAML");

        request.putQueryParameter("RoleArn", roleArn);
        request.putQueryParameter("SAMLProviderArn", samlProviderArn);
        request.putQueryParameter("SAMLAssertion", samlAssertion);
        CommonResponse response = client.getCommonResponse(request);
        return response;
    }

    /**
     * 通过临时AK & Token获取登录Token
     *
     * @param commonResponse 临时AK & Token
     * @return 登录Token
     * @throws IOException
     */
    public static String requestSignInToken(CommonResponse commonResponse) throws IOException {
        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");

        String signInTokenUrl = SIGN_IN_HOST + String.format(
            "/federation?Action=GetSigninToken"
                + "&AccessKeyId=%s"
                + "&AccessKeySecret=%s"
                + "&SecurityToken=%s&TicketType=mini",
            URLEncoder.encode(credentials.getString("AccessKeyId"), "utf-8"),
            URLEncoder.encode(credentials.getString("AccessKeySecret"), "utf-8"),
            URLEncoder.encode(credentials.getString("SecurityToken"), "utf-8")
        );

        HttpGet signInGet = new HttpGet(signInTokenUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(signInGet);
        String signInToken = "";
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            String signInRes = EntityUtils.toString(httpResponse.getEntity());
            logger.info("received signInRes: {}", signInRes);
            signInToken = JSON.parseObject(signInRes).getString("SigninToken");

            if (signInToken == null) {
                logger.error("Invalid response message, contains no SigninToken: {}", signInRes);
            }
        } else {
            logger.error("Failed to retrieve signInToken");
        }

        return signInToken;
    }

    /**
     * 通过登录token生成日志服务web访问链接进行跳转
     *
     * @param signInToken 登录token
     * @param project     项目名称
     * @param logstore    日志库名称
     * @return 免登录Url
     * @throws UnsupportedEncodingException
     */
    public static String generateSignInUrl(String signInToken, String project, String logstore)
        throws UnsupportedEncodingException {
        String slsUrl = String.format("https://sls4service.console.aliyun.com/next"
                + "/project/%s"
                + "/logsearch/%s?hiddenBack=true&hiddenChangeProject=true&hiddenOverview=true&hideTopbar=true",
            URLEncoder.encode(project, "utf-8"),
            URLEncoder.encode(logstore, "utf-8"));

        String signInUrl = SIGN_IN_HOST + String.format(
            "/federation?Action=Login"
                + "&LoginUrl=%s"
                + "&Destination=%s"
                + "&SigninToken=%s",
            URLEncoder.encode("https://www.aliyun.com", "utf-8"),
            URLEncoder.encode(slsUrl, "utf-8"),
            URLEncoder.encode(signInToken, "utf-8"));
        return signInUrl;
    }

    public static String drawWithColor(String action) {
        String actionWithColor = action;
        if ("install".equals(action) || "Success".equals(action)) {
            actionWithColor = "<font color=\"green\">" + action + "</font>";
        } else if ("uninstall".equals(action) || "Failed".equals(action)) {
            actionWithColor = "<font color=\"red\">" + action + "</font>";
        } else if ("create".equals(action)) {
            actionWithColor = "<font color=\"green\">+ " + action + "</font>";
        } else if ("delete".equals(action)) {
            actionWithColor = "<font color=\"red\">- " + action + "</font>";
        }

        return actionWithColor;
    }
}
