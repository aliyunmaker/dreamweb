package cc.landingzone.dreamweb.service;

import java.util.List;

import cc.landingzone.dreamweb.dao.ApiUserDao;
import cc.landingzone.dreamweb.model.ApiUser;
import cc.landingzone.dreamweb.utils.KeyGenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
@Component
public class ApiUserService {

    @Autowired
    private ApiUserDao apiUserDao;

    /**
     * 添加API账号
     *
     * @param apiUser
     */
    @Transactional
    public void addApiUser(ApiUser apiUser) {
        Assert.notNull(apiUser, "数据不能为空!");
        Assert.notNull(apiUser.getValid(), "请设置API账号是否生效!");
        apiUser.setAccessKeyId(KeyGenerationUtils.generateAccessKeyId());
        apiUser.setAccessKeySecret(KeyGenerationUtils.generateAccessKeySecret(apiUser.getAccessKeyId()));
        apiUserDao.addApiUser(apiUser);
    }

    /**
     * 更新API账号信息
     *
     * @param apiUser
     */
    @Transactional
    public void updateApiUser(ApiUser apiUser){
        Assert.notNull(apiUser, "数据不能为空!");
        Assert.hasText(apiUser.getAccessKeyId(), "accessKeyId不能为空!");
        Assert.hasText(apiUser.getAccessKeySecret(), "accessKeySecret不能为空!");
        Assert.notNull(apiUser.getValid(), "请设置API账号是否生效!");
        apiUserDao.updateApiUser(apiUser);
    }

    /**
     * 根据ID获取API账号
     *
     * @param id
     * @return
     */
    public ApiUser getApiUserById(Integer id) {
        Assert.notNull(id, "id不能为空!");
        return apiUserDao.getApiUserById(id);
    }

    /**
     * 获取所有API账号
     *
     * @return
     */
    public List<ApiUser> getAllApiUsers() {
        return apiUserDao.getAllApiUsers();
    }

    /**
     * 删除API账号
     *
     * @param id
     */
    @Transactional
    public void deleteApiUser(Integer id) {
        Assert.notNull(id, "id can not be null!");
        apiUserDao.deleteApiUser(id);
    }
}
