package cc.landingzone.dreamweb.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.SystemConfig;

@Component
public interface SystemConfigDao {

    List<SystemConfig> listSystemConfig();

    SystemConfig getSystemConfig(String configName);

    void addSystemConfig(SystemConfig systemConfig);

    void deleteSystemConfig(Integer id);

}
