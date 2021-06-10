package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.SlsConfigDao;
import cc.landingzone.dreamweb.model.SlsConfig;
import cc.landingzone.dreamweb.model.SlsConfigInfo;
import cc.landingzone.dreamweb.model.User;
import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class SlsConfigService {

    private static Logger logger = LoggerFactory.getLogger(LoginRecordService.class);

    @Autowired
    SlsConfigDao slsConfigDao;

    @Autowired
    UserService userService;

    /**
     * SLS配置缓存，未命中则调用CacheLoader的load方法。value不允许为空，用Optional解决
     */
    private LoadingCache<String, Optional<SlsConfig>> cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .refreshAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Optional<SlsConfig>>() {
                       @Override
                       public Optional<SlsConfig> load(String userName) {
                           User user = userService.getUserByLoginName(userName);
                           SlsConfig slsConfig = slsConfigDao.getSlsConfigByOwnerId(user.getId());
                           return Optional.ofNullable(slsConfig);
                       }
                   }
            );


    public List<SlsConfig> listSlsConfig() {
        return slsConfigDao.listSlsConfig();
    }

    public SlsConfig getSlsConfigById(Integer id) {
        Assert.notNull(id, "id不能为空");
        return slsConfigDao.getSlsConfigById(id);
    }

    public SlsConfig getSlsConfigByOwnerId(Integer configOwnerId) {
        Assert.notNull(configOwnerId, "ownerId不能为空");
        return slsConfigDao.getSlsConfigByOwnerId(configOwnerId);
    }

    @Transactional
    public void addSlsConfig(SlsConfig slsConfig) {
        Assert.notNull(slsConfig, "数据不能为空!");
        // 获取userId
        Assert.isNull(slsConfigDao.getSlsConfigByOwnerId(slsConfig.getConfigOwnerId()), "同一账号不能重复配置SLS，请删除原有配置或在原有配置上修改");
        slsConfigDao.addSlsConfig(slsConfig);
        logger.info("成功添加SLS配置数据：{}", slsConfig);
    }

    public void updateSlsConfig(SlsConfig slsConfig) {
        Assert.notNull(slsConfig, "数据不能为空!");
        slsConfigDao.updateSlsConfig(slsConfig);
        logger.info("成功修改SLS配置数据：{}", slsConfig);
    }

    public void deleteSlsConfig(Integer id) {
        Assert.notNull(id, "id不能为空");
        slsConfigDao.deleteSlsConfig(id);
    }


    /**
     * 从缓存中获取当前用户的SLS配置信息
     * @return
     */
    public SlsConfigInfo getSlsConfigInfoFromCache() throws ExecutionException {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<SlsConfig> slsConfigOptional = cache.get(userName);
        SlsConfig slsConfig = slsConfigOptional.orElse(null);

        SlsConfigInfo slsConfigInfo = null;
        if(slsConfig != null) {
            slsConfigInfo = JSON.parseObject(slsConfig.getConfigValue()).toJavaObject(SlsConfigInfo.class);
        }

        return slsConfigInfo;
    }
}
