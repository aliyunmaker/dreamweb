package cc.landingzone.dreamweb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.utils.HttpClientUtils;
import cc.landingzone.dreamweb.utils.JsonUtils;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

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

}