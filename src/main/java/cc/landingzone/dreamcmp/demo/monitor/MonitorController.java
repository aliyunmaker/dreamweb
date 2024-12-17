package cc.landingzone.dreamcmp.demo.monitor;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.common.utils.AliyunAPIUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class MonitorController extends BaseController {

    @Value("${dreamcmp.aliyun_grafana_api_key}")
    private String GRAFANA_API_KEY;

    @RequestMapping("/getFailureStatus.do")
    public void getFailureStatus(HttpServletRequest request, HttpServletResponse response) {
        // TODO 调FC -> 调系统接口 获取故障状态
    }

    @RequestMapping("/simulateFailure.do")
    public void simulateFailure(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // TODO 调FC -> 调系统接口 模拟/恢复系统故障
        result.setSuccess(true);
        outputToJSON(response, result);
    }

    @RequestMapping("/getGrafanaDashboard.do")
    public void getGrafanaDashboard(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            long start = System.currentTimeMillis();
            String redirectUrl = "https://grafana-rngkaxtoijlr5xxe72.grafana.aliyuncs.com/d/be7f0355-969a-419d-983e-91bf8d3f9be9/5LiA5bGP5oC76KeI5aSn55uY?from=now-15m&to=now&kiosk=1&refresh=10s&view&aliyun_api_key=" + GRAFANA_API_KEY;
            response.sendRedirect(redirectUrl);
            logger.info("signSLSMonitor cost: " + (System.currentTimeMillis() - start) + "ms");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getTracing.do")
    public void getTracing(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            // String destination = "https://tracing-analysis.console.aliyun.com/?hideTopbar=true&hideSidebar=true#/appList/cn-hangzhou"; // 旧版
            String destination = "https://trace4service.console.aliyun.com/#/tracing/cn-hangzhou?appId=benwhzyqus%4080e77f717b662da&tab=appTopu&source=XTRACE&xtraceType=trace&from=now%2Fd&to=now%2Fd&refresh=10s";
            String redirectUrl = getRedirectUrl(destination);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getSLSLogstore.do")
    public void getSLSLogstore(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String projectName = request.getParameter("projectName");
            String logStore = request.getParameter("logStore");
            String queryString = request.getParameter("queryString");
            String destination = "https://sls4service.console.aliyun.com/lognext/project/" + projectName + "/logsearch/" + logStore + "?hideTopbar=true&hideSidebar=true&ignoreTabLocalStorage=true";
            if (StringUtils.isNotEmpty(queryString)) {
                destination += "&queryString=" + queryString;
            }
            String redirectUrl = getRedirectUrl(destination);
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
    }

    private static @NotNull String getRedirectUrl(String destination) throws Exception {
        String signToken = getSignToken();
        String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
            + URLEncoder.encode(destination, StandardCharsets.UTF_8.displayName())
            + "&LoginUrl=https%3a%2f%2faliyun.com&SigninToken="
            + signToken;
        return redirectUrl;
    }

    private static String getSignToken() throws Exception {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
            CommonConstants.Aliyun_AccessKeySecret,
            CommonConstants.ADMIN_ROLE_ARN, username, "", true);
        return signToken;
    }

}