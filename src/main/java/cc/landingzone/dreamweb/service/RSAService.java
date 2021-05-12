package cc.landingzone.dreamweb.service;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.security.KeyPair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.binary.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cc.landingzone.dreamweb.dao.RSADao;
import cc.landingzone.dreamweb.model.RSAKey;
import cc.landingzone.dreamweb.utils.RSAEncryptUtils;

@Component
public class RSAService {
    @Autowired
    private RSADao rsaDao;

    // private static String publicKey;
    // private static String privateKey;
    // private static boolean hasInitKey = false;
    private Lock lock = new ReentrantLock();

    public static Logger logger = LoggerFactory.getLogger(RSAService.class.getName());


    /**
     * 随机生成公私钥对，并设置publicKey和privateKey
     * @throws NoSuchAlgorithmException
     */
    // void genKeyPair() throws NoSuchAlgorithmException {
    //     // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
    //     KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
    //     // 初始化密钥对生成器，密钥大小为96-1024位
    //     keyPairGen.initialize(1024, new SecureRandom());
    //     // 生成一个密钥对，保存在keyPair中
    //     KeyPair keyPair = keyPairGen.generateKeyPair();
    //     RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
    //     RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 得到公钥
    //     String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
    //     // 得到私钥字符串
    //     String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
    //     RSAService.publicKey = publicKeyString;
    //     RSAService.privateKey = privateKeyString;
    // }


    /**
     * 从数据库读取公私钥对（rsakey数据库暂时最多只存一条数据）
     * @return 公私钥对，可能为空
     */
    Map.Entry<String, String> getKeyPairFromDB() {
        List<RSAKey> rl = rsaDao.getKeyPair();
        if(rl.size() == 0) {
            return null;
        }else{
            RSAKey res = rl.get(0);
            return new AbstractMap.SimpleEntry<String, String>(res.getPublicKey(),
            res.getPrivateKey());
        }
    }

    /**
     * 获取公私钥对，若数据库中数据有则从数据库中读取；
     * 否则，则先随机生成公私钥对，并插入到数据库中
     * @return 公私钥对，可能为空
     */
    private Map.Entry<String, String> GetKey() {
        Map.Entry<String, String> keyPair = null;
        lock.lock();
        try {
            keyPair = getKeyPairFromDB();
            if(keyPair == null) {
                java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                keyPair = RSAEncryptUtils.genKeyPair();
                rsaDao.setKeyPair(new RSAKey(keyPair.getKey(), keyPair.getValue()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            lock.unlock();
        }
        return keyPair == null ? null : new AbstractMap.SimpleEntry<String, String>(keyPair);
    }

    /**
     * 更新数据库中的公私钥对
     */
    public void UpdateKey() {
        lock.lock();
        try {
            Map.Entry<String, String> keyPair = getKeyPairFromDB();
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Map.Entry<String, String> newKeyPair = RSAEncryptUtils.genKeyPair();
            if(keyPair == null) {
                rsaDao.setKeyPair(new RSAKey(newKeyPair.getKey(), newKeyPair.getValue()));
            }else {
                rsaDao.updateKeyPair(new RSAKey(newKeyPair.getKey(), newKeyPair.getValue()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            lock.unlock();
        }
    }


    /**
     * 获取数据库中的公钥，public方法
     * @return 公钥
     */
    public String getPublicKey() {
        Map.Entry<String, String> keyPair = GetKey();
        return keyPair == null ? null : keyPair.getKey();
    }


    /**
     * 获取数据库中的私钥，private方法，供decrypt方法使用
     * @return 私钥
     */
    private String getPrivateKey() {
        Map.Entry<String, String> keyPair = GetKey();
        return keyPair == null ? null : keyPair.getValue();
    }


    /**
     * RSA私钥解密
     * @param str 加密字符串
     * @return 明文
     */
    public String decrypt(String str) {
        try {
            return RSAEncryptUtils.decrypt(str, getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
