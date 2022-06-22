package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.controller.UserController;
import cc.landingzone.dreamweb.dao.ProductDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品信息相关操作
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public class ProductService {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    @Transactional
    public List<String> listApplication () {
        return productDao.listApplication();
    }

    @Transactional
    public List<String> listScenes (String application) {
        return productDao.listScenes(application);
    }

    @Transactional
    public String getProductId (String application, String scene) {
        return productDao.getProductId(application, scene);
    }

    @Transactional
    public String getProductName (String productId) {
        return productDao.getProductName(productId);
    }

    @Transactional
    public List<Product> listProduct(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<Product> list = productDao.listProduct(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = productDao.getProductTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

    @Transactional
    public void saveProduct(Product product) {
        Product product2 = getProductByProductId(product.getProductId());
        if (product2 != null) {
            throw new IllegalArgumentException("此产品ID(" + product2.getProductId()+ ")已存在");
        }
        productDao.saveProduct(product);
    }

    @Transactional
    public Product getProductByProductId(String productId) {
        Assert.hasText(productId, "产品ID不能为空!");
        return productDao.getProductByProductId(productId);
    }

    @Transactional
    public Product getProductById(Integer id) {
        return productDao.getProductById(id);
    }

    @Transactional
    public void updateProduct(Product product) {
        Assert.notNull(product, "数据不能为空!");
        Assert.hasText(product.getProductId(), "产品ID不能为空!");
        productDao.updateProduct(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        productDao.deleteProduct(id);
    }

}
