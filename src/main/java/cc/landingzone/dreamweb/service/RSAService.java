package cc.landingzone.dreamweb.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.landingzone.dreamweb.dao.RSADao;
import cc.landingzone.dreamweb.model.RSAKey;
import cc.landingzone.dreamweb.utils.RSAEncryptUtils;

@Component
public class RSAService {

    @Autowired
    private RSADao rsaDao;

    public static Logger logger = LoggerFactory.getLogger(RSAService.class);

    private static final String KEYNAME = "systemRSAKey";

    /**
     * 获取系统密钥，若数据库中有则从数据库中读取； 否则，则先随机生成公密钥对，并插入到数据库中
     * 
     * @return 公私钥对，可能为空
     */
    private RSAKey getRSAKey() {
        RSAKey rsaKey = rsaDao.getRSAKeyByName(KEYNAME);
        if (rsaKey == null) {
            try {
                Map.Entry<String, String> keyPair = RSAEncryptUtils.genKeyPair();
                rsaKey = new RSAKey();
                rsaKey.setPublicKey(keyPair.getKey());
                rsaKey.setPrivateKey(keyPair.getValue());
                rsaKey.setKeyName(KEYNAME);
                if (rsaDao.addRSAKey(rsaKey) == 0) {
                    rsaKey = rsaDao.getRSAKeyByName(KEYNAME);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return rsaKey;
    }

    /**
     * 随机生成新的密钥对并更新数据库中的系统密钥
     */
    public void updateRSAKey() {
        RSAKey rsaKey = rsaDao.getRSAKeyByName(KEYNAME);
        try {
            Map.Entry<String, String> keyPair = RSAEncryptUtils.genKeyPair();
            rsaKey.setPublicKey(keyPair.getKey());
            rsaKey.setPrivateKey(keyPair.getValue());
            rsaDao.updateRSAKey(rsaKey);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取数据库中的私钥，private方法，供decrypt方法使用
     * 
     * @return 私钥
     */
    private String getPrivateKey() {
        RSAKey rsaKey = getRSAKey();
        return rsaKey == null ? null : rsaKey.getPrivateKey();
    }

    /**
     * 获取数据库中的公钥，public方法
     * 
     * @return 公钥
     */
    public String getPublicKey() {
        RSAKey rsaKey = getRSAKey();
        return rsaKey == null ? null : rsaKey.getPublicKey();
    }

    /**
     * RSA私钥解密
     * 
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
