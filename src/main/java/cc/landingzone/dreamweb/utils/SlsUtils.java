package cc.landingzone.dreamweb.utils;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class SlsUtils {
    private static Logger logger = LoggerFactory.getLogger(SignatureUtils.class);

    private static final String STS_HOST = "sts.aliyuncs.com";
    private static final String SIGN_IN_HOST = "http://signin.aliyun.com";

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

    /**
     * 生成Authorization
     * @param httpGet Get请求，携带有请求头
     * @param secretKey SLS配置中的AccessKeySecret
     * @return 该httpGet请求的数字签名
     * @throws UnsupportedEncodingException
     */
    public static String generateAuthorization(HttpGet httpGet, String accessKey, String secretKey) throws UnsupportedEncodingException {
        // 1. 根据Http头信息，生成UTF-8编码的签名字符串
        String signString = generateSignString(httpGet);
        String signStringUTF8 = new String(signString.getBytes(), "utf-8");

        // 2. 使用Hmac-sha1算法加密
        HmacUtils hm1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey);
        byte[] encryptedSignString = hm1.hmac(signStringUTF8);

        // 3. 使用Base64编码
        String signature = Base64.getEncoder().encodeToString(encryptedSignString);
        String authorization = "LOG " + accessKey + ":" + signature;

        return authorization;
    }

    /**
     * 根据Get请求，生成签名
     * @param httpGet 请求信息
     * @return
     */
    private static String generateSignString(HttpGet httpGet) {
        // 1. 获取签名所需的请求method
        String method = httpGet.getMethod();

        // 2. 获取签名所需的访问resource
        String resourcePattern = "http://.*(/.*)";
        Pattern pattern = Pattern.compile(resourcePattern);
        Matcher matcher = pattern.matcher(httpGet.getURI().toString());

        String canonicalizedResource = "";
        if(matcher.find()) {
            canonicalizedResource = matcher.group(1);
        }

        // 3. 根据Http的请求头，拼接生成签名所需的canonicalizedLogHeaders
        Header[] headers = httpGet.getAllHeaders();
        // 请求头按字典序排序
        Arrays.sort(headers, Comparator.comparing(NameValuePair::getName));
        // 以x-log和x-acs开头的签名需要被拼接
        String logHeaderPattern = "^((x-log)|(x-acs)).*";

        StringBuilder canonicalizedLogHeadersBuilder = new StringBuilder();
        for(Header header : headers) {
            String key = header.getName();
            String value = header.getValue();
            if(Pattern.matches(logHeaderPattern, key)) {
                canonicalizedLogHeadersBuilder.append(key).append(":").append(value);
                canonicalizedLogHeadersBuilder.append("\n");
            }
        }

        String canonicalizedLogHeaders;
        if(canonicalizedLogHeadersBuilder.length() == 0) {
            canonicalizedLogHeaders = "";
        } else {
            canonicalizedLogHeaders = canonicalizedLogHeadersBuilder
                    .deleteCharAt(canonicalizedLogHeadersBuilder.length()-1)
                    .toString();
        }

        // 4. 添加Date
        String GMTDate = generateGMTDate();
        httpGet.setHeader("Date", GMTDate);


        // 5. 将各个组件拼接程签名字符串
        String signString = generateSignString(method,
                "",
                "",
                GMTDate,
                canonicalizedLogHeaders,
                canonicalizedResource);
        return signString;
    }

    /**
     * 根据各个组件拼接生成签名字符串
     * @param verb HTTP请求方法
     * @param contentMD5 HTTP请求Body部分的MD5值，必须大写字符串
     * @param contentType HTTP请求中Body部分类型
     * @param date HTTP请求中的标准时间戳
     * @param canonicalizedLogHeaders HTTP请求中以x-log和x-acs为前缀的自定义头构造的字符串
     * @param canonicalizedResource HTTP请求资源构造的字符串
     * @return 签名字符串
     */
    private static String generateSignString(String verb,
                                             String contentMD5,
                                             String contentType,
                                             String date,
                                             String canonicalizedLogHeaders,
                                             String canonicalizedResource) {
        String signString = verb + "\n"
                + contentMD5 + "\n"
                + contentType + "\n"
                + date + "\n"
                + canonicalizedLogHeaders + "\n"
                + canonicalizedResource;

        return signString;
    }

    /**
     * 生成GMT标准时间戳
     * @return
     */
    private static String generateGMTDate() {
        SimpleDateFormat sdf3 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf3.setTimeZone(TimeZone.getTimeZone("GMT"));
        String rfc1123 = sdf3.format(new Date());
        return rfc1123;
    }
}
