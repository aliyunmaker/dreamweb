package cc.landingzone.dreamweb.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.TypeReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.model.ApiUser;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.model.enums.LoginMethodEnum;
import cc.landingzone.dreamweb.service.ApiUserService;
import cc.landingzone.dreamweb.service.LoginRecordService;
import cc.landingzone.dreamweb.service.SystemConfigService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.HttpClientUtils;
import cc.landingzone.dreamweb.utils.JsonUtils;
import cc.landingzone.dreamweb.utils.SignatureUtils;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private ApiUserService apiUserService;
    @Autowired
    private LoginRecordService loginRecordService;
    @Autowired
    private SystemConfigService systemConfigService;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping("/")
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.sendRedirect("index.html");
        } catch (Exception e) {
        }

    }

    @RequestMapping("/index.html")
    public String hello(Model model) {
        model.addAttribute("loginPageTitle", systemConfigService.getLoginPageTitle());
        model.addAttribute("isAdmin", AuthorityUtils.authorityListToSet(
            SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            ).contains("ROLE_ADMIN"));
        model.addAttribute("allowSolutionDemo", systemConfigService.isAllowSolutionDemo());
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginPageTitle", systemConfigService.getLoginPageTitle());
        model.addAttribute("allowWechatLogin", systemConfigService.isAllowWechatLogin());
        return "login";
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
                user.setLoginMethod(LoginMethodEnum.WEIXIN_LOGIN);
                user.setLoginName(UserService.WX_UNION_LOGIN_NAME_PREFIX + unionid);
                user.setName(userInfoMap.get("nickname"));
                user.setUnionid(unionid);
                user.setComment(LoginMethodEnum.WEIXIN_LOGIN.getComment());
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

            // 创建登录记录
            loginRecordService.addLoginRecord(request, user.getLoginName(), LoginMethodEnum.WEIXIN_LOGIN);

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

    /**
     * 通过token自动登录
     *
     * @param request
     * @param response
     */
    @RequestMapping("/autoLogin")
    public void autoLogin(HttpServletRequest request, HttpServletResponse response) {
        long now = System.currentTimeMillis();

        WebResult result = new WebResult();
        try {
            String token = request.getParameter("token");
            Assert.hasText(token, "token不能为空!");

            // BASE64解码
            String params = new String(Base64.getUrlDecoder().decode(token.getBytes(StandardCharsets.UTF_8)));
            Map<String, String> paramMap = Arrays.stream(params.split("&"))
                    .collect(Collectors.toMap(kv -> kv.split("=")[0], kv -> kv.split("=")[1]));

            String accessKeyId = paramMap.get("accessKeyId");
            Assert.hasText(accessKeyId, "accessKeyId不能为空!");

            String signature = paramMap.get("signature");
            Assert.hasText(signature, "signature不能为空!");

            String timestamp = paramMap.get("timestamp");
            Assert.hasText(timestamp, "timestamp不能为空!");

            String loginName = paramMap.get("loginName");
            Assert.hasText(loginName, "loginName不能为空!");

            ApiUser apiUser = apiUserService.getApiUserByAccessKeyId(accessKeyId);
            Assert.notNull(apiUser, "API账号不存在!");
            if (!apiUser.getValid()) {
                throw new RuntimeException("API账号未生效!");
            }

            // 构造params, key1=value1&key2=value2...
            params = paramMap.entrySet().stream().filter(entry -> !"signature".equals(entry.getKey()))
                    .sorted(Map.Entry.comparingByKey()).map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            // 校验签名
            SignatureUtils.checkSignature(params, apiUser.getAccessKeySecret(), signature);
            // 检查签名是否过期
            SignatureUtils.checkTimestamp(now, timestamp);

            // 查询用户信息
            User user = userService.getUserByLoginName(loginName);
            if (null == user) {
                user = new User();
                user.setLoginName(loginName);
                user.setLoginMethod(LoginMethodEnum.AUTO_LOGIN);
                user.setName(loginName);
                user.setRole("ROLE_GUEST");
                user.setComment(LoginMethodEnum.AUTO_LOGIN.getComment());
                userService.addUser(user);
            }

            // 给用户授权
            List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority(user.getRole()));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLoginName(),
                    user.getLoginName(), grantedAuths);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 创建登录记录
            loginRecordService.addLoginRecord(request, loginName, LoginMethodEnum.AUTO_LOGIN);

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
}