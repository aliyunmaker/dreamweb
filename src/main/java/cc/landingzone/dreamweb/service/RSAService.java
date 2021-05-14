package cc.landingzone.dreamweb.service;

import java.util.AbstractMap;
import java.util.List;
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
    
    public static Logger logger = LoggerFactory.getLogger(RSAService.class.getName());


    /*----------------------private method----------------------*/
    /**
     * 从数据库读取公私钥对（rsakey数据库暂时最多只存一条数据）
     * @return 公私钥对，可能为空
     */
    private Map.Entry<String, String> getKeyPairFromDB() {
        List<RSAKey> rsaKeyList = rsaDao.getKeyPair("systemRSAKey");    
        if(rsaKeyList == null || rsaKeyList.size() == 0) {
            return null;
        }else{
            RSAKey res = rsaKeyList.get(0);
            return new AbstractMap.SimpleEntry<String, String>(res.getPublicKey(), res.getPrivateKey());
        }
    }

    /**
     * 将密钥对存到数据库中
     * @param keyPair 随机生成的密钥对
     */
    private void setKeyPairToDB(Map.Entry<String, String> keyPair) {
        RSAKey rsaKey = new RSAKey();
        rsaKey.setPublicKey(keyPair.getKey());
        rsaKey.setPrivateKey(keyPair.getValue());
        rsaKey.setKeyName("systemRSAKey");
        rsaDao.setKeyPair(rsaKey);
    }

    /**
     * 将密钥对更新到数据库中
     * @param keyPair 随机生成的密钥对
     */
    private void updateKeyPairToDB(Map.Entry<String, String> keyPair) {
        RSAKey rsaKey = new RSAKey();
        rsaKey.setPublicKey(keyPair.getKey());
        rsaKey.setPrivateKey(keyPair.getValue());
        rsaKey.setKeyName("systemRSAKey");
        rsaDao.updateKeyPair(rsaKey);
    }

    /**
     * 获取公私钥对，若数据库中数据有则从数据库中读取；
     * 否则，则先随机生成公私钥对，并插入到数据库中
     * @return 公私钥对，可能为空
     */
    private Map.Entry<String, String> getKeyPair() {
        Map.Entry<String, String> keyPair = null;
        keyPair = getKeyPairFromDB();
        if(keyPair == null) {
            try {
                keyPair = RSAEncryptUtils.genKeyPair();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            setKeyPairToDB(keyPair);
        }
        return keyPair == null ? null : new AbstractMap.SimpleEntry<String, String>(keyPair);
    }

    /**
     * 获取数据库中的私钥，private方法，供decrypt方法使用
     * @return 私钥
     */
    private String getPrivateKey() {
        Map.Entry<String, String> keyPair = getKeyPair();
        return keyPair == null ? null : keyPair.getValue();
    }


    /*----------------------public method----------------------*/
    /**
     * 获取数据库中的公钥，public方法
     * @return 公钥
     */
    public String getPublicKey() {
        Map.Entry<String, String> keyPair = getKeyPair();
        return keyPair == null ? null : keyPair.getKey();
    }

    /**
     * 更新数据库中的公私钥对
     */
    public void updateRSAKey() {
        try {
            Map.Entry<String, String> keyPair = getKeyPairFromDB();
            Map.Entry<String, String> newKeyPair = RSAEncryptUtils.genKeyPair();
            if(keyPair == null) {
                setKeyPairToDB(newKeyPair);
            }else {
                updateKeyPairToDB(newKeyPair);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
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
