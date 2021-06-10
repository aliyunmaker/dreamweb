package cc.landingzone.dreamweb.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

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
     * @return 临时AK和Token
     * @throws ClientException
     */
    public static AssumeRoleResponse requestAccessKeyAndSecurityToken(String region, String accessKey, String secretKey, String roleArn, String roleSession) throws ClientException {
        DefaultProfile.addEndpoint("", region, "Sts", STS_HOST);
        IClientProfile profile = DefaultProfile.getProfile(region, accessKey, secretKey);
        DefaultAcsClient client = new DefaultAcsClient(profile);

        AssumeRoleRequest assumeRoleReq = new AssumeRoleRequest();
        assumeRoleReq.setRoleArn(roleArn);
        assumeRoleReq.setRoleSessionName(roleSession);
        assumeRoleReq.setMethod(MethodType.POST);
        assumeRoleReq.setDurationSeconds(3600L); // 过期时间，单位为秒，默认3600
        // 默认可以不需要setPolicy，即申请获得角色的所有权限
        // assumeRoleReq.setPolicy(本次生成token实际需要的权限字符串，申请权限必须是角色对应权限的子集);
        // 权限示例参考链接：https://help.aliyun.com/document_detail/89676.html

        AssumeRoleResponse assumeRoleRes = client.getAcsResponse(assumeRoleReq);
        return assumeRoleRes;
    }

    /**
     * 通过临时AK & Token获取登录Token
     * @param assumeRoleRes 临时AK & Token
     * @return 登录Token
     * @throws IOException
     */
    public static String requestSignInToken(AssumeRoleResponse assumeRoleRes) throws IOException {
        String signInTokenUrl = SIGN_IN_HOST + String.format(
                "/federation?Action=GetSigninToken"
                        + "&AccessKeyId=%s"
                        + "&AccessKeySecret=%s"
                        + "&SecurityToken=%s&TicketType=mini",
                URLEncoder.encode(assumeRoleRes.getCredentials().getAccessKeyId(), "utf-8"),
                URLEncoder.encode(assumeRoleRes.getCredentials().getAccessKeySecret(), "utf-8"),
                URLEncoder.encode(assumeRoleRes.getCredentials().getSecurityToken(), "utf-8")
        );

        HttpGet signInGet = new HttpGet(signInTokenUrl); //创建HttpGet请求
        CloseableHttpClient httpClient = HttpClients.createDefault(); // 创建HttpClient
        HttpResponse httpResponse = httpClient.execute(signInGet); // 用HttpClient执行 HttpGet请求
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
     * @param signInToken 登录token
     * @param project 项目名称
     * @param logstore 日志库名称
     * @return 免登录Url
     * @throws UnsupportedEncodingException
     */
    public static String generateSignInUrl(String signInToken, String project, String logstore) throws UnsupportedEncodingException {
        String slsUrl = String.format("https://sls4service.console.aliyun.com/next"
                        + "/project/%s/logsearch"
                        + "/%s?isShare=true&hideTopbar=true&hideSidebar=true",
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
}
