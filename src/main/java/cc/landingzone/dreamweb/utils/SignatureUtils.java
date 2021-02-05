package cc.landingzone.dreamweb.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author merc-bottle
 * @date 2021/02/03
 */
public class SignatureUtils {

    private static Logger logger = LoggerFactory.getLogger(SignatureUtils.class);

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long THIRTY_MINUTES = 30 * ONE_MINUTE;

    /**
     * 校验签名
     *
     * @param params
     * @param accessKeySecret
     * @param signature
     */
    public static void checkSignature(String params, String accessKeySecret, String signature) {
        Assert.isTrue(signature.equals(generateSignature(params, accessKeySecret)), "签名验证失败!");
    }

    /**
     * 计算签名
     *
     * @param needSign 待签名字符串
     * @param accessKeySecret 通过DreamWeb系统申请的AccessKey Secret
     * @return
     */
    public static String generateSignature(String needSign, String accessKeySecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec signingKey = new SecretKeySpec(accessKeySecret.getBytes(DEFAULT_CHARSET), mac.getAlgorithm());
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(needSign.getBytes(DEFAULT_CHARSET));
            return toHexString(rawHmac);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 把二进制转化为小写的十六进制
     *
     * @param bytes
     * @return
     */
    public static String toHexString(final byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 检查签名是否过期
     *
     * @param now
     * @param timestamp
     */
    public static void checkTimestamp(long now, String timestamp) {
        Assert.isTrue(Math.abs(now - Long.parseLong(timestamp)) <= THIRTY_MINUTES, "签名已经过期!");
    }
}
