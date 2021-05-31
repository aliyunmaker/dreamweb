package cc.landingzone.dreamweb.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.SystemConfig;

@Component
public interface SystemConfigDao {

    List<SystemConfig> listSystemConfig();

    SystemConfig getSystemConfigById(Integer id);

    SystemConfig getSystemConfigByName(String configName);

    void addSystemConfig(SystemConfig systemConfig);

    void addUnChangeableSystemConfig(SystemConfig systemConfig);

    void updateSystemConfig(SystemConfig systemConfig);

    void deleteSystemConfig(Integer id);

}
