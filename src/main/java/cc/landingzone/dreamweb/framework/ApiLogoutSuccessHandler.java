package cc.landingzone.dreamweb.framework;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

/**
 * @author merc-bottle
 * @date 2021/02/03
 */
public class ApiLogoutSuccessHandler implements LogoutSuccessHandler {

    private String targetUrl;

    public ApiLogoutSuccessHandler(String targetUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(targetUrl), "defaultTarget must start with '/' or with 'http(s)'");
        this.targetUrl = targetUrl;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.sendRedirect(targetUrl);
    }
}
