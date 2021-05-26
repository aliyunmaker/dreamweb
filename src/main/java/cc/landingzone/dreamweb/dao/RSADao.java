package cc.landingzone.dreamweb.dao;

import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.SystemConfig;

@Component
public interface RSADao {

    SystemConfig getRSAKeyByName(String keyName);

    int addRSAKey(SystemConfig rsaKey);

    void updateRSAKey(SystemConfig rsaKey);

}
