package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductDao;
import cc.landingzone.dreamweb.dao.UserProductDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.UserProductAssociate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private ProductDao productDao;

    @Transactional
    public List<String> listProductId(String userName) {
        return userProductDao.listProductId(userName);
    }

    @Transactional
    public List<Product> listProductByUserId(Integer userId) {
        List<Integer> productIds = userProductDao.listProductIdsByUserId(userId);
        List<Product> products = new ArrayList<>();
        for (Integer productId: productIds ) {
            Product product = productDao.getProductById(productId);
            products.add(product);
        }
        return products;
    }

    @Transactional
    public List<UserProductAssociate> listUserProductAssociate(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<UserProductAssociate> list = userProductDao.listUserProductAssociate(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = userProductDao.getUserProductAssociateTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

    @Transactional
    public void saveUserProductAssociate(UserProductAssociate userProductAssociate) {
        UserProductAssociate userProductAssociate1 = getUserProductAssociateByProductIdAndUserId(userProductAssociate.getProductId(), userProductAssociate.getUserId());
        if (userProductAssociate1 != null) {
            throw new IllegalArgumentException("此权限已存在！");
        }
        userProductDao.saveUserProductAssociate(userProductAssociate);
    }

    @Transactional
    public UserProductAssociate getUserProductAssociateByProductIdAndUserId(Integer productId, Integer userId) {
        return userProductDao.getUserProductAssociateByProductIdAndUserId(productId, userId);
    }

    @Transactional
    public UserProductAssociate getUserProductById(Integer id) {
        return userProductDao.getUserProductById(id);
    }

    @Transactional
    public void updateUserProductAssociate(UserProductAssociate userProductAssociate) {
        UserProductAssociate userProductAssociate1 = getUserProductAssociateByProductIdAndUserId(userProductAssociate.getProductId(), userProductAssociate.getUserId());
        if (userProductAssociate1 != null) {
            throw new IllegalArgumentException("此权限已存在！");
        }
        userProductDao.updateUserProductAssociate(userProductAssociate);
    }

    @Transactional
    public void deleteUserProductAssociate(Integer id) {
        userProductDao.deleteUserProductAssociate(id);
    }

    @Transactional
    public String getServicecatalogPortfolioId (Integer productId, Integer userId) {
        return userProductDao.getServicecatalogPortfolioId(productId, userId);
    }

}
