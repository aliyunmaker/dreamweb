package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.UserProductDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.UserProduct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<String> listProductId(String userName) {
        return userProductDao.listProductId(userName);
    }

    @Transactional
    public List<Product> listProduct(String userName) {
        return userProductDao.listProduct(userName);
    }

    @Transactional
    public List<UserProduct> listUserProduct(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<UserProduct> list = userProductDao.listUserProduct(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = userProductDao.getUserProductTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

    @Transactional
    public void saveUserProduct(UserProduct userProduct) {
        UserProduct userProduct1 = getUserProduct(userProduct.getProductId(), userProduct.getUserName());
        if (userProduct1 != null) {
            throw new IllegalArgumentException("此权限已存在！");
        }
        userProductDao.saveUserProduct(userProduct);
    }

    @Transactional
    public UserProduct getUserProduct(String productId, String userName) {
        return userProductDao.getUserProduct(productId, userName);
    }

    @Transactional
    public UserProduct getUserProductById(Integer id) {
        return userProductDao.getUserProductById(id);
    }

    @Transactional
    public void updateUserProduct(UserProduct userProduct) {
        userProductDao.updateUserProduct(userProduct);
    }

    @Transactional
    public void deleteUserProduct(Integer id) {
        userProductDao.deleteUserProduct(id);
    }

}
