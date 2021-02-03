package cc.landingzone.dreamweb.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.TypeReference;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.model.ApiUser;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ApiUserService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.HttpClientUtils;
import cc.landingzone.dreamweb.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApiUserService apiUserService;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long THIRTY_MINUTES = 30 * ONE_MINUTE;

    @RequestMapping("/")
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("index.html");
        } catch (Exception e) {
        }

    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/apiLogin")
    public void apiLogin(HttpServletRequest request, HttpServletResponse response) {
        long now = System.currentTimeMillis();

        WebResult result = new WebResult();
        try {
            String accessKeyId = request.getParameter("accessKeyId");
            Assert.hasText(accessKeyId, "accessKeyId不能为空!");

            String signature = request.getParameter("signature");
            Assert.hasText(signature, "signature不能为空!");

            String timestamp = request.getParameter("timestamp");
            Assert.hasText(timestamp, "timestamp不能为空!");

            // String signatureNonce = request.getParameter("signatureNonce");
            // Assert.hasText(signatureNonce, "signatureNonce must not be empty");

            String loginName = request.getParameter("loginName");
            Assert.hasText(loginName, "loginName不能为空!");

            ApiUser apiUser = apiUserService.getApiUserByAccessKeyId(accessKeyId);
            Assert.notNull(apiUser, "API账号不存在!");
            if (!apiUser.getValid()) {
                throw new RuntimeException("API账号未生效!");
            }

            // 构造params, key1=value1&key2=value2...
            String params = Collections.list(request.getParameterNames()).stream()
                .filter(parameterName -> !"signature".equals(parameterName))
                .sorted()
                .map(parameterName -> parameterName + "=" + request.getParameter(parameterName))
                .collect(Collectors.joining("&"));

            // 校验签名
            checkSignature(params, apiUser.getAccessKeySecret(), signature);
            // 检查签名是否过期
            checkTimestamp(now, timestamp);

            // 查询用户信息
            User user = userService.getUserByLoginName(loginName);
            if (null == user) {
                user = new User();
                user.setLoginName(loginName);
                user.setName(loginName);
                user.setRole("ROLE_GUEST");
                userService.addUser(user);
            }

            // 给用户授权
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority(user.getRole()));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLoginName(),
                user.getLoginName(), grantedAuths);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if ("ROLE_ADMIN".equals(user.getRole())) {
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/welcome/welcome.html");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());

            outputToJSON(response, result);
        }
    }

    /**
     * 网站微信登录回调
     *
     * @param request
     * @param response
     */
    @RequestMapping("/weixin/web_login_callback.do")
    public void web_login_callback(HttpServletRequest request, HttpServletResponse response) {
        String result = "";
        try {
            String code = request.getParameter("code");
            String sessionUrl = String.format(
                    "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    CommonConstants.WEB_LANDINGZONE_ID, CommonConstants.WEB_LANDINGZONE_SECRET, code);
            String sessionResult = HttpClientUtils.getDataAsStringFromUrl(sessionUrl);
            logger.info("sessionResult:{}", sessionResult);
            Map<String, String> sessionMap = JsonUtils.parseObject(sessionResult,
                    new TypeReference<Map<String, String>>() {
                    });
            String unionid = sessionMap.get("unionid");
            User user = userService.getUserByUnionid(unionid);

            String userInfoUrl = String.format("https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s",
                    sessionMap.get("access_token"), sessionMap.get("openid"));
            String userInfoResult = HttpClientUtils.getDataAsStringFromUrl(userInfoUrl);
            logger.info("userInfoResult:{}", userInfoResult);
            Map<String, String> userInfoMap = JsonUtils.parseObject(userInfoResult,
                    new TypeReference<Map<String, String>>() {
                    });

            if (null == user) {
                user = new User();
                user.setLoginName(UserService.WX_UNION_LOGIN_NAME_PREFIX + unionid);
                user.setName(userInfoMap.get("nickname"));
                user.setUnionid(unionid);
                user.setComment("login from website by weixin qrcode");
                user.setRole(UserService.User_Role_Guest);
                userService.addUser(user);
            } else {
                // 修正用户名和openid,统一以weixin_unionId为准
                user.setLoginName(UserService.WX_UNION_LOGIN_NAME_PREFIX + unionid);
                user.setName(userInfoMap.get("nickname"));
                user.setUnionid(unionid);
                userService.updateUser(user);
            }
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority(user.getRole()));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLoginName(),
                    user.getLoginName(), grantedAuths);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if ("ROLE_ADMIN".equals(user.getRole())) {
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/welcome/welcome.html");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = e.getMessage();
        }
        outputToString(response, result);
    }

    private static void checkSignature(String params, String accessKeySecret, String signature) {
        Assert.isTrue(signature.equals(generateSignature(params, accessKeySecret)), "签名验证失败!");
    }

    private static String generateSignature(String params, String accessKeySecret) {
        String stringToSign = params;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec signingKey = new SecretKeySpec(accessKeySecret.getBytes(DEFAULT_CHARSET), mac.getAlgorithm());
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(stringToSign.getBytes(DEFAULT_CHARSET));
            return toHexString(rawHmac);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 把二进制转化为小写的十六进制
     *
     * @param bytes
     * @return
     */
    private static String toHexString(final byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static void checkTimestamp(long now, String timestamp) {
        Assert.isTrue(Math.abs(now - Long.parseLong(timestamp)) <= THIRTY_MINUTES, "签名已经过期!");
    }

    /**
     * 生成API登录需要的参数
     */
    private static void generateApiLoginParams() {
        // 创建参数表
        Map<String, String> params = new HashMap<>();
        // Access Key ID
        params.put("accessKeyId", CommonConstants.TEST_API_ACCESS_KEY_ID);
        // 13位时间戳
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        // 自定义参数
        params.put("loginName", CommonConstants.TEST_LOGIN_NAME);

        // 按照参数名称的字典顺序对请求中所有参数(不包括signature参数本身)进行排序
        String[] keyArray = params.keySet().toArray(new String[0]);
        Arrays.sort(keyArray);

        // 将参数名称和值用"="进行连接,得到形如"key=value"的字符串
        // 将"="连接得到的参数组合按顺序依次用"&"进行连接,得到形如"key1=value1&key2=value2..."的字符串
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (String key : keyArray) {
            if (!isFirst) {
                stringBuilder.append("&");
            } else {
                isFirst = false;
            }
            stringBuilder.append(key).append("=").append(params.get(key));
        }
        String needSignature = stringBuilder.toString();

        // 生成签名
        String signature = generateSignature(needSignature, CommonConstants.TEST_API_ACCESS_KEY_SECRET);
        System.out.println(needSignature + "&signature=" + signature);
    }

    public static void main(String[] args) {
        generateApiLoginParams();
    }
}