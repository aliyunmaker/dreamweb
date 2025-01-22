package cc.landingzone.dreamcmp.demo.monitor;

import cc.landingzone.dreamcmp.demo.workshop.service.FcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.common.utils.AliyunAPIUtils;
import com.alibaba.fastjson.JSON;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class MonitorController extends BaseController {

    @Value("${dreamcmp.aliyun_grafana_url}")
    private String GRAFANA_URL;

    @Value("${dreamcmp.aliyun_monitoring_function_name}")
    private String SIMULATE_ERROR_FC_FUNCTION_NAME;

    @Autowired
    FcService fcService;

    @RequestMapping("/getSimulateErrorStatus.do")
    public void getSimulateErrorStatus(HttpServletRequest request, HttpServletResponse response) {
        // 调FC -> 调系统接口 获取故障状态
        WebResult result = new WebResult();
        String data = fcService.invokeFunctionAsync(SIMULATE_ERROR_FC_FUNCTION_NAME, "getSimulateErrorStatus");
        Object error = Optional.ofNullable(JSON.parseObject(data))
            .map(x -> x.get("data"))
            .orElse(null);
        result.setData(error);
        System.out.println(JSON.toJSONString(result));
        outputToJSON(response, result);
    }

    @RequestMapping("/simulateError.do")
    public void simulateError(HttpServletRequest request, HttpServletResponse response) {
        // 调FC -> 调系统接口 模拟故障/恢复正常
        WebResult result = new WebResult();
        String data = fcService.invokeFunctionAsync(SIMULATE_ERROR_FC_FUNCTION_NAME, "simulateError");
        result.setData(JSON.parseObject(data));
        System.out.println(JSON.toJSONString(result));
        outputToJSON(response, result);
    }

    @RequestMapping("/getGrafanaDashboard.do")
    public void getGrafanaDashboard(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            long start = System.currentTimeMillis();
            String redirectUrl = GRAFANA_URL;
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
            String destination = "https://trace4service.console.aliyun.com/?hideTopbar=true&hideSidebar=true#/cn-hangzhou/tracing-explorer?source=XTRACE&from=now-15m&to=now&refresh=10s&slsFilters=(serviceName%20%3A%20%22dreamone-customer-system%22%20or%20serviceName%20%3A%20%22dreamone-order-system%22%20or%20serviceName%20%3A%20%22dreamone-item-system%22%20)&filters=serviceName%20IN%20(%22dreamone-customer-system%22%20%2C%20%22dreamone-order-system%22%20%2C%20%22dreamone-item-system%22)";
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