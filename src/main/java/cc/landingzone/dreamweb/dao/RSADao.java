package cc.landingzone.dreamweb.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.RSAKey;

@Component
public interface RSADao {
    List<RSAKey> getKeyPair(String keyName);

    void setKeyPair(RSAKey keyPair);

    void updateKeyPair(RSAKey keyPair);
}
