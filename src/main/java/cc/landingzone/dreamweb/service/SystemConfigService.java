package cc.landingzone.dreamweb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cc.landingzone.dreamweb.dao.SystemConfigDao;
import cc.landingzone.dreamweb.model.SystemConfig;

@Component
public class SystemConfigService {

    @Autowired
    private SystemConfigDao systemConfigDao;

    /**
     * 获取所有系统配置
     * 
     * @return
     */
    public List<SystemConfig> listSystemConfig() {
        return systemConfigDao.listSystemConfig();
    }

    /**
     * 通过名字获得配置
     * 
     * @param configName
     * @return
     */
    public SystemConfig getSystemConfigByName(String configName) {
        Assert.hasText(configName, "配置名不能为空");
        return systemConfigDao.getSystemConfig(configName);
    }

    /**
     * 添加系统配置
     *
     * @param systemConfig
     * @throws Exception
     */
    @Transactional
    public void addSystemConfig(SystemConfig systemConfig) throws DuplicateKeyException {
        Assert.notNull(systemConfig, "数据不能为空!");
        try {
            systemConfigDao.addSystemConfig(systemConfig);
        } catch (DuplicateKeyException e) {
            throw new DuplicateKeyException("该配置名已存在!");
        }
    }

    /**
     * 删除系统配置
     *
     * @param id
     */
    @Transactional
    public void deleteSystemConfig(Integer id) {
        Assert.notNull(id, "id can not be null!");
        systemConfigDao.deleteSystemConfig(id);
    }
    
}
