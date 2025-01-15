package cc.landingzone.dreamcmp.demo.workshop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.landingzone.dreamcmp.demo.workshop.service.OssService;
import cc.landingzone.dreamcmp.demo.workshop.service.StsService;
import cc.landingzone.dreamcmp.demo.workshop.service.SlsService;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.model.ListObjectsV2Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aliyun.sts20150401.models.GetCallerIdentityResponse;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.model.WebResult;

@RestController
@RequestMapping(value = "workshop/ak")
public class DeAkDemoController extends BaseController {

    @Autowired
    private StsService stsService;

    @Autowired
    private OssService ossService;

    @Autowired
    private SlsService slsService;

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
}