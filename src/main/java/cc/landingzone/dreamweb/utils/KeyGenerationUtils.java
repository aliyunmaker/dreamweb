package cc.landingzone.dreamweb.utils;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.util.Assert;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class KeyGenerationUtils {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateAccessKeyId() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    public static String generateAccessKeySecret(String accessKeyId) {
        Assert.hasText(accessKeyId, "accessKeyId can not be blank!");
        return Md5Utils.getMD5(accessKeyId + generateUUID());
    }

    public static void main(String[] args) {
        String accessKeyId = generateAccessKeyId();
        System.out.println(accessKeyId);
        String secretAccessKey = generateAccessKeySecret(accessKeyId);
        System.out.println(secretAccessKey);
    }
}