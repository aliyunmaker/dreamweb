package cc.landingzone.dreamweb.service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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
    private LoadingCache<Integer, Map.Entry<String, String>> cache = CacheBuilder.newBuilder()
        .maximumSize(1)
        .expireAfterWrite(30, TimeUnit.SECONDS)
        .build(new CacheLoader<Integer, Map.Entry<String, String>>(){
            public Map.Entry<String, String> load(Integer key) {
                return getKeyPairFromDB();
            }
        });
    
    public static Logger logger = LoggerFactory.getLogger(RSAService.class.getName());


    /*----------------------private method----------------------*/
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
    private Map.Entry<String, String> getKeyPairFromDB() {
        List<RSAKey> rsaKeyList = null;
        lock.lock();
        try {
            rsaKeyList = rsaDao.getKeyPair();    
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            rsaDao.setKeyPair(new RSAKey(keyPair.getKey(), keyPair.getValue()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 将密钥对更新到数据库中
     * @param keyPair 随机生成的密钥对
     */
    private void updateKeyPairToDB(Map.Entry<String, String> keyPair) {
        lock.lock();
        try {
            rsaDao.updateKeyPair(new RSAKey(keyPair.getKey(), keyPair.getValue()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            lock.unlock();
        }
    }

    /**
     * 获取公私钥对，若数据库中数据有则从数据库中读取；
     * 否则，则先随机生成公私钥对，并插入到数据库中
     * @return 公私钥对，可能为空
     */
    private Map.Entry<String, String> GetKeyPair() {
        Map.Entry<String, String> keyPair = null;
        keyPair = getKeyPairFromDB();
        // try {
        //     keyPair = cache.get(0);
        // } catch (Exception e) {
        //     //防止数据库里没数据，cache拿到空数据抛出异常
        // }
        if(keyPair == null) {
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            try {
                keyPair = RSAEncryptUtils.genKeyPair();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            setKeyPairToDB(keyPair);
            // cache.put(0, keyPair);
        }
        return keyPair == null ? null : new AbstractMap.SimpleEntry<String, String>(keyPair);
    }

    /**
     * 获取数据库中的私钥，private方法，供decrypt方法使用
     * @return 私钥
     */
    private String getPrivateKey() {
        Map.Entry<String, String> keyPair = GetKeyPair();
        return keyPair == null ? null : keyPair.getValue();
    }


    /*----------------------public method----------------------*/
    /**
     * 获取数据库中的公钥，public方法
     * @return 公钥
     */
    public String getPublicKey() {
        Map.Entry<String, String> keyPair = GetKeyPair();
        return keyPair == null ? null : keyPair.getKey();
    }

    /**
     * 更新数据库中的公私钥对
     */
    public void UpdateKey() {
        try {
            Map.Entry<String, String> keyPair = getKeyPairFromDB();
            // Map.Entry<String, String> keyPair = cache.get(0);
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
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
