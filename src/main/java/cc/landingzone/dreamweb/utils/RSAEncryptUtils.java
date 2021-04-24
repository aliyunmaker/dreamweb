package cc.landingzone.dreamweb.utils;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.AbstractMap;
import java.util.Map;

public class RSAEncryptUtils {

    public static String publicKey;
    private static String privateKey;

    public static Logger logger = LoggerFactory.getLogger(RSAEncryptUtils.class.getName());

    static {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
        try {
//            publicKey = CharStreams.toString(new InputStreamReader(RSAEncryptUtils.class.getResourceAsStream("/ssocert/rsa_public.pem"), StandardCharsets.UTF_8));
//            privateKey = CharStreams.toString(new InputStreamReader(RSAEncryptUtils.class.getResourceAsStream("/ssocert/rsa_private.pem"), StandardCharsets.UTF_8));
//
//            publicKey = publicKey
//                    .replace("-----BEGIN PUBLIC KEY-----", "")
//                    .replaceAll(System.lineSeparator(), "")
//                    .replace("-----END PUBLIC KEY-----", "");
//            privateKey = privateKey
//                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
//                    .replaceAll(System.lineSeparator(), "")
//                    .replace("-----END RSA PRIVATE KEY-----", "");
            Map.Entry<String, String> keyPair = genKeyPair();
            publicKey = keyPair.getKey();
            privateKey = keyPair.getValue();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println(publicKey);
        System.out.println(privateKey);
        String message = "1111";
        String messageEn = encrypt(message, publicKey);
        System.out.println(messageEn);

        String messageDe = decrypt(messageEn, privateKey);
        System.out.println(messageDe);
    }

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static Map.Entry<String, String> genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        Map.Entry<String, String> result = new AbstractMap.SimpleEntry<String, String>(publicKeyString, privateKeyString);
        return result;
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static String decrypt(String str) {
        try {
            return decrypt(str, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


}
