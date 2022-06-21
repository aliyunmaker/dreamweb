package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.UserProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 查询用户有权限的产品列表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public class UserProductService {

    @Autowired
    private UserProductDao userProductDao;

    @Transactional
    public List<String> getProductId(String username) {
        return userProductDao.getProductId(username);
    }
}
