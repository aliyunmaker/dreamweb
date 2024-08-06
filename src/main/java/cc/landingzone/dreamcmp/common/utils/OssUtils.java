package cc.landingzone.dreamcmp.common.utils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;

import cc.landingzone.dreamcmp.common.CommonConstants;

public class OssUtils {

    private static Logger logger = LoggerFactory.getLogger(OssUtils.class);

    public static final String BUCKET_DREAMWEB = "dreamweb";
    private static final String OSS_URL_HANGZHOU = "http://oss-cn-hangzhou.aliyuncs.com";

    public static OSSClient ossClient = new OSSClient(OSS_URL_HANGZHOU,
        new DefaultCredentialProvider(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret),
        null);

    /**
     * 将url文件上传到oss
     *
     * @param url 文件地址
     * @param bucketName
     * @param key 文件全路径,可以包含目录
     * @throws Exception
     */
    public static String uploadWebFileToOss(String url, String bucketName, String key) throws Exception {
        URL imageUrl = new URL(url);
        // 以杭州为例
        // String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        // 初始化一个OSSClient
        // OSSClient client = new OSSClient(OSS_URL_HANGZHOU, ACCESS_ID,
        // ACCESS_KEY);

        URLConnection con = imageUrl.openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(20000);
        InputStream content = con.getInputStream();

        // 获取指定文件的输入流
        // File file = new File(filePath);
        // InputStream content = imageUrl.openStream();// new
        // FileInputStream(file);

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        // meta.setContentLength(100);

        // 上传Object.
        PutObjectResult result = ossClient.putObject(bucketName, key, content, meta);
        logger.info("upload success,etag:" + result.getETag());
        return result.getETag();
    }

    public static String generatePresignedUrl(String key, String contentType) {
        Calendar expireTime = Calendar.getInstance();
        expireTime.add(Calendar.MINUTE, 5);
        // URL url = ossClient.generatePresignedUrl(BUCKET_ICHENGCHAO, key, expireTime.getTime(), HttpMethod.PUT);
        // return url.toString();
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_DREAMWEB, key, HttpMethod.PUT);
        // request.setContentType("text/plain; charset=ISO-8859-1");
        // 这个地方非常重要，签名校验失败的时候，大概率是这里的问题
        // request.setContentType("text/plain;charset=UTF-8");
        if (StringUtils.isNotBlank(contentType)) {
            request.setContentType(contentType);
        }
        request.setExpiration(expireTime.getTime());
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString().replace("http", "https");
    }

    public static Map<String, String> generatePostSignature() throws Exception {
        String dir = "download/";
        Calendar expireTime = Calendar.getInstance();
        expireTime.add(Calendar.MINUTE, 5);
        // PostObject请求最大可支持的文件大小为500MB
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 500 * 1024 * 1024);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = ossClient.generatePostPolicy(expireTime.getTime(), policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);

        Map<String, String> result = new HashMap<>();
        result.put("policy", encodedPolicy);
        result.put("policyOrigin", postPolicy);
        result.put("signature", postSignature);
        result.put("OSSAccessKeyId", CommonConstants.Aliyun_AccessKeyId);
        result.put("dir", dir);
        return result;
    }

}
