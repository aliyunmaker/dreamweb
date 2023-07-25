package cc.landingzone.dreamcmp.demo.dailyinspection;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamcmp.demo.dailyinspection.model.Rule;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/inspection")
public class DailyInspectionController extends BaseController {
    @RequestMapping("/listRules.do")
    public void listRules(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            List<Rule> rules = DailyInspectionUtil.listRules();
            result.setTotal(rules.size());
            result.setData(rules);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/getRuleDetail.do")
    public void getRuleDetail(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ruleId = request.getParameter("ruleId");
            JSONObject rule = DailyInspectionUtil.getRule(ruleId);
            result.setTotal(rule.size());
            result.setData(rule);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/activateRules.do")
    public void activateRules(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ruleIds = request.getParameter("ruleIds");
            String requestId = DailyInspectionUtil.activateRules(ruleIds);
            result.setData(requestId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/deactivateRules.do")
    public void deactivateRules(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String ruleIds = request.getParameter("ruleIds");
            String requestId = DailyInspectionUtil.deactivateRules(ruleIds);
            result.setData(requestId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @RequestMapping("/signInResourceInfo.do")
    public void signInResourceInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            String resourceId = request.getParameter("resourceId");
            String resourceType = request.getParameter("resourceType");
            String regionId = request.getParameter("regionId");
            String url = "https://config.console.aliyun.com/resources/-/detail/" + resourceType + "/" + resourceId + "?region=" + regionId;

            String signToken = AliyunAPIUtils.getSigninToken(CommonConstants.Aliyun_AccessKeyId,
                    CommonConstants.Aliyun_AccessKeySecret,
                    CommonConstants.ADMIN_ROLE_ARN, username, "", false);

            String redirectUrl = "https://signin.aliyun.com/federation?Action=Login&Destination="
                    + URLEncoder.encode(url, StandardCharsets.UTF_8.displayName())
                    + "&LoginUrl=https%3a%2f%2faliyun.com&SigninToken="
                    + signToken;
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}
