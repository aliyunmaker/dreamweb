package cc.landingzone.dreamweb.dao;

import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.RSAKey;

@Component
public interface RSADao {
    
    RSAKey getRSAKeyByName(String keyName);

    int addRSAKey(RSAKey rsaKey);

    void updateRSAKey(RSAKey rsaKey);
}
