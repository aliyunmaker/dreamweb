package cc.landingzone.dreamweb.service;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.landingzone.dreamweb.dao.RSADao;
import cc.landingzone.dreamweb.model.SystemConfig;
import cc.landingzone.dreamweb.utils.JsonUtils;
import cc.landingzone.dreamweb.utils.RSAEncryptUtils;

@Component
public class RSAService {

    @Autowired
    private RSADao rsaDao;

    @Autowired
    SystemConfigService systemConfigService;

    private static Logger logger = LoggerFactory.getLogger(RSAService.class);

    private static final String CONFIG_NAME = "systemRSAKey";

    /**
     * 随机生成密钥对
     * 
     * @return 密钥对
     * @throws NoSuchAlgorithmException
     */
    private RSAKeyPair genRSAKeyPair() throws NoSuchAlgorithmException {
        Map.Entry<String, String> keyPair = RSAEncryptUtils.genKeyPair();
        RSAKeyPair rsaKeyPair = new RSAKeyPair();
        rsaKeyPair.setPublicKey(keyPair.getKey());
        rsaKeyPair.setPrivateKey(keyPair.getValue());
        return rsaKeyPair;
    }

    /**
     * 获取系统密钥，若数据库中有则从数据库中读取； 否则，则先随机生成公密钥对，并插入到数据库中
     * 
     * @return 密钥对，可能为空
     */
    private RSAKeyPair getRSAKeyPair() {
        SystemConfig rsaKey = rsaDao.getRSAKeyByName(CONFIG_NAME);
        if (rsaKey == null) {
            try {
                RSAKeyPair rsaKeyPair = genRSAKeyPair();
                rsaKey = new SystemConfig();
                rsaKey.setConfigName(CONFIG_NAME);
                rsaKey.setConfigValue(JsonUtils.toJsonString(rsaKeyPair));
                if (rsaDao.addRSAKey(rsaKey) == 0) {
                    rsaKey = rsaDao.getRSAKeyByName(CONFIG_NAME);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        RSAKeyPair rsaKeyPair = JsonUtils.parseObject(rsaKey.getConfigValue(), RSAKeyPair.class);
        return rsaKeyPair;
    }

    /**
     * 随机生成新的密钥对并更新数据库中的系统密钥
     */
    public void updateRSAKey() {
        SystemConfig rsaKey = rsaDao.getRSAKeyByName(CONFIG_NAME);
        try {
            RSAKeyPair rsaKeyPair = genRSAKeyPair();
            rsaKey.setConfigValue(JsonUtils.toJsonString(rsaKeyPair));
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
        RSAKeyPair rsaKeyPair = getRSAKeyPair();
        return rsaKeyPair == null ? null : rsaKeyPair.getPrivateKey();
    }

    /**
     * 获取数据库中的公钥，public方法
     * 
     * @return 公钥
     */
    public String getPublicKey() {
        RSAKeyPair rsaKeyPair = getRSAKeyPair();
        return rsaKeyPair == null ? null : rsaKeyPair.getPublicKey();
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

class RSAKeyPair {

    private String publicKey;
    private String privateKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

}
