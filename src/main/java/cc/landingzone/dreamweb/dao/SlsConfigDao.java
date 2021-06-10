package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.SlsConfig;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SlsConfigDao {
    List<SlsConfig> listSlsConfig();

    SlsConfig getSlsConfigById(Integer id);

    SlsConfig getSlsConfigByOwnerId(Integer configOwnerId);

    void addSlsConfig(SlsConfig systemConfig);

    void updateSlsConfig(SlsConfig systemConfig);

    void deleteSlsConfig(Integer id);
}
