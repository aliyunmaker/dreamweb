package cc.landingzone.dreamcmp.demo.oss;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cc.landingzone.dreamcmp.common.BaseController;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.model.WebResult;
import cc.landingzone.dreamcmp.common.utils.AliyunAPIUtils;
import cc.landingzone.dreamcmp.common.utils.OssUtils;

@Controller
@RequestMapping("/ossdemo")
public class OssDemoController extends BaseController {

    @RequestMapping("/getSTSToken.do")
    public void getSTSToken(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Map<String, String> data = AliyunAPIUtils.getSTSToken(CommonConstants.Aliyun_AccessKeyId,
                CommonConstants.Aliyun_AccessKeySecret, "acs:ram::1158528183198580:role/dreamweb-oss",
                AliyunAPIUtils.Policy_OSS_FULL, Duration.ofHours(1).getSeconds());
            data.put("region", "oss-cn-hangzhou");
            data.put("bucket", OssUtils.BUCKET_DREAMWEB);
            result.setData(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
     * 生成OSS的预签名URL
     * 
     * @param request
     * @param response
     */
    @RequestMapping("/generatePresignedUrl.do")
    public void generatePresignedUrl(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String contentType = "text/plain;charset=UTF-8";
            String key = "download/demo.txt";
            String url = OssUtils.generatePresignedUrl("download/demo.txt", contentType);
            Map<String, String> data = new HashMap<String, String>();
            data.put("url", url);
            data.put("key", OssUtils.BUCKET_DREAMWEB + "/" + key);
            data.put("contentType", contentType);
            data.put("httpMethod", "PUT");
            result.setData(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

    /**
     * 生成OSS的Postpolicy的签名
     * 
     * @param request
     * @param response
     */
    @RequestMapping("/generatePostSignature.do")
    public void generatePostSignature(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            Map<String, String> data = OssUtils.generatePostSignature();
            result.setData(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
        outputToJSON(response, result);
    }

}