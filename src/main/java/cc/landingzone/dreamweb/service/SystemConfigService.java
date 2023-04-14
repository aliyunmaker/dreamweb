package cc.landingzone.dreamweb.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import cc.landingzone.dreamweb.dao.SystemConfigDao;
import cc.landingzone.dreamweb.model.SystemConfig;

@Component
public class SystemConfigService {

    @Autowired
    private SystemConfigDao systemConfigDao;

    private LoadingCache<String, Optional<String>> cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .refreshAfterWrite(1, TimeUnit.SECONDS)
        .build(new CacheLoader<String, Optional<String>>() {
                @Override
                public Optional<String> load(String key) {
                    SystemConfig systemConfig = systemConfigDao.getSystemConfigByName(key);
                    return Optional.ofNullable(systemConfig).map(SystemConfig::getConfigValue);
                }
            }
        );

    /**
     * 获取所有系统配置
     * 
     * @return
     */
    public List<SystemConfig> listSystemConfig() {
        return systemConfigDao.listSystemConfig();
    }

    /**
     * 通过id获得配置
     * 
     * @param id
     * @return
     */
    public SystemConfig getSystemConfigById(Integer id) {
        Assert.notNull(id, "id不能为空");
        return systemConfigDao.getSystemConfigById(id);
    }

    /**
     * 通过配置名获得配置
     * 
     * @param configName 配置名
     * @return
     */
    public SystemConfig getSystemConfigByName(String configName) {
        Assert.hasText(configName, "配置名不能为空!");
        return systemConfigDao.getSystemConfigByName(configName);
    }

    /**
     * 通过配置名获得配置字符串值
     * 
     * @param configName
     * @return configValue
     */
    public String getStringValue(String configName) {
        Assert.hasText(configName, "配置名不能为空!");
        SystemConfig systemConfig = systemConfigDao.getSystemConfigByName(configName);
        return Optional.ofNullable(systemConfig).map(SystemConfig::getConfigValue).orElse(null);
    }

    /**
     * 通过配置名获得布尔值，若忽略大小写后为true则返回true，否则返回false
     * 
     * @param configName
     * @return
     */
    public boolean getBoolValue(String configName) {
        return Boolean.parseBoolean(getStringValue(configName));
    }

    /**
     * 添加系统配置
     *
     * @param systemConfig
     * @throws Exception
     */
    @Transactional
    public void addSystemConfig(SystemConfig systemConfig) {
        Assert.notNull(systemConfig, "数据不能为空!");
        systemConfigDao.addSystemConfig(systemConfig);
    }

    /**
     * 更新系统配置
     * 
     * @param systemConfig
     */
    public void updateSystemConfig(SystemConfig systemConfig) {
        Assert.notNull(systemConfig, "数据不能为空!");
        systemConfigDao.updateSystemConfig(systemConfig);
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


    /**
     * ################################################
     * ###################使用缓存方法###################
     * ################################################
     */

    /**
     * 从缓存通过配置名获得配置字符串值
     * 
     * @param configName
     * @return configValue
     */
    public String getStringValueFromCache(String configName) {
        Assert.hasText(configName, "配置名不能为空!");
        return cache.getUnchecked(configName).orElse(null);
    }

    public String getStringValueFromCache(String configName, String defaultValue) {
        Assert.hasText(configName, "配置名不能为空!");
        return cache.getUnchecked(configName).orElse(defaultValue);
    }

    /**
     * 从缓存通过配置名获得布尔值，若忽略大小写后为true则返回true，否则返回false
     * 
     * @param configName
     * @return
     */
    public boolean getBooleanValueFromCache(String configName) {
        Assert.hasText(configName, "配置名不能为空!");
        return cache.getUnchecked(configName).map(Boolean::parseBoolean).orElse(false);
    }


    /**
     * ################################################
     * ###################特定配置方法###################
     * ################################################
     */

    /**
     * 是否允许使用微信登录，若配置不存在则返回false
     * 
     * @return
     */
    public boolean isAllowWechatLogin() {
        return getBooleanValueFromCache("allowWechatLogin");
    }

    /**
     * 是否允许使用LDAP登录，若配置不存在则返回false
     * 
     * @return
     */
    public boolean isAllowLDAP() {
        return getBooleanValueFromCache("allowLDAP");
    }

    /**
     * 登录页标题
     * 
     * @return
     */
    public String getLoginPageTitle() {
        return getStringValueFromCache("loginPageTitle", "个人演示网");
    }

    /**
     * 是否有解决方案Demo的选项
     * 
     * @return
     */
    public boolean isAllowSolutionDemo() {
        return getBooleanValueFromCache("allowSolutionDemo");
    }
}
