package cc.landingzone.dreamcmp.demo.workshop;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamcmp.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamcmp.common.utils.HttpClientUtils;
import cc.landingzone.dreamcmp.demo.workshop.service.OssService;
import cc.landingzone.dreamcmp.demo.workshop.service.StsService;
import cc.landingzone.dreamcmp.demo.workshop.service.SlsService;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aliyun.sts20150401.models.GetCallerIdentityResponse;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.model.WebResult;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "workshop/ak")
public class AkLessDemoController extends BaseController {

    private static final String COOKIE_NAME = "cookie_dreamcmp";
    private static final int AUTH_CODE_EXPIRATION_MINUTES = 5;
    private static final Map<String, LocalDateTime> authCodes = new ConcurrentHashMap<>();

    @Value("${dreamcmp.workshop.assume_role_arn}")
    private String roleArn;

    @Autowired
    private StsService stsService;

    @Autowired
    private OssService ossService;

    @Autowired
    private SlsService slsService;

    @PostMapping("/generateAuthCode.do")
    public void generateAuthCode(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            // Generate random verification code
            String code = UUID.randomUUID().toString().substring(0, 8);
            
            // Set expiration time
            authCodes.put(code, LocalDateTime.now().plusMinutes(AUTH_CODE_EXPIRATION_MINUTES));
            
            // Create secure cookie
            // ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, code)
            //     .maxAge(-1)
            //     .secure(true)
            //     .httpOnly(true)
            //     .sameSite("Strict")
            //     .path("/workshop")
            //     .build();
            // Add cookie to response
            // response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            result.setData(code);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        Iterator<Map.Entry<String, LocalDateTime>> iterator = authCodes.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, LocalDateTime> entry = iterator.next();
            if (now.isAfter(entry.getValue())) {
                iterator.remove();
                logger.info("Removed expired verification code: " + entry.getKey());
            }
        }
    }

    private String getAuthCodeFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    String domain = cookie.getDomain();
                    System.out.println(domain);


                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isValidAuthCode(String code) {
        if (code == null) {
            return false;
        }
        
        LocalDateTime expirationTime = authCodes.get(code);
        if (expirationTime == null) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(expirationTime)) {
            authCodes.remove(code);
            return false;
        }
        
        return true;
    }

    @PostMapping("/getStsToken.do")
    public void getStsToken(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        
        // Verify the code from param
        String verificationCode = request.getParameter("code");
        if (!isValidAuthCode(verificationCode)) {
            result.setSuccess(false);
            result.setErrorMsg("Unauthorized!");
            outputToJSON(response, result);
            return;
        }

        // demo policy
        String usage = request.getParameter("Usage");
        String sessionPolicy = AliyunAPIUtils.Policy_DENY;
        if ("oss".equals(usage)) {
            sessionPolicy = AliyunAPIUtils.Policy_OSS_FULL;
        } else if ("sls".equals(usage)) {
            sessionPolicy = AliyunAPIUtils.Policy_SLS_FULL;
        }
        
        try {
            AssumeRoleRequest req = new AssumeRoleRequest();
            req.setRoleArn(roleArn);
            req.setRoleSessionName("workshop");
            req.setDurationSeconds(900L);
            req.setPolicy(sessionPolicy);
            AssumeRoleResponse resp = stsService.assumeRole(req);
            result.setData(resp.getBody().getCredentials());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping("/getCallerIdentity.do")
    public void getCallerIdentity(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            GetCallerIdentityResponse resp = stsService.getCallerIdentity();
            result.setData(resp);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping("/listOssObjects.do")
    public void listOssObjects(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String prefix = request.getParameter("prefix");
            ListObjectsV2Result listObjectsV2Result = ossService.listObjects(prefix);

            JSONObject data = new JSONObject();
            data.put("files", listObjectsV2Result.getObjectSummaries());
            data.put("folders", listObjectsV2Result.getCommonPrefixes());
            result.setData(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping("/getOssObject.do")
    public void getOssObject(HttpServletRequest request, HttpServletResponse response) {
        try {
            String key = request.getParameter("key");
            if (key == null || key.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            byte[] content = ossService.getObject(key);
            
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + 
                             java.net.URLEncoder.encode(key.substring(key.lastIndexOf("/") + 1), "UTF-8"));
            response.setContentLength(content.length);
            
            // 写入响应
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getSlsUrl.do")
    public void getSlsUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String url = slsService.getSignedSlsUrl();
            result.setData(url);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping("/putLog.do")
    public void putLog(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String log = request.getParameter("log");
            if (log == null || log.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMsg("日志内容不能为空");
            } else {
                slsService.putLog(log);
                result.setSuccess(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    @PostMapping("/getMetadata.do")
    public void getMetadata(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String url = request.getParameter("url");
            if (url == null || !url.startsWith("http://100.100.100.200/")) {
                result.setSuccess(false);
                result.setErrorMsg("Invalid metadata URL");
                outputToJSON(response, result);
                return;
            }

            String data = HttpClientUtils.getDataAsStringFromUrl(url);
            result.setData(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }
}