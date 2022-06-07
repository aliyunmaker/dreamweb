package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


import java.util.*;

@Component
public class ProductService {
    @Autowired
    private ProductDao productDao;

    @Transactional
    public List<String> getApplication () {
        return productDao.getApplication();
    }

    @Transactional
    public List<String> getScenes (String application) {
        return productDao.getScenes(application);
    }

    @Transactional
    public String getProductId (String application, String scene) {
        return productDao.getProductId(application, scene);
    }

    @Transactional
    public Integer getExampleId (String productId, String exampleName) {
        return productDao.getExampleId(productId, exampleName);
    }

    @Transactional
    public void addExample (String productId, String exampleName) {
        productDao.addExample(productId, exampleName);
    }

    @Transactional
    public List<Product> searchProduct(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<Product> list = productDao.searchProduct(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = productDao.searchProductTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

    @Transactional
    public void addProduct(Product product) {
        Product product2 = getProductByProductId(product.getProductid());
        if (product2 != null) {
            throw new IllegalArgumentException("此产品ID(" + product2.getProductid()+ ")已存在");
        }
        productDao.addProduct(product);
    }

    public Product getProductByProductId(String productId) {
        Assert.hasText(productId, "产品ID不能为空!");
        return productDao.getProductByProductId(productId);
    }

    public Product getProductById(Integer id) {
        return productDao.getProductById(id);
    }

    @Transactional
    public void updateProduct(Product product) {
        Assert.notNull(product, "数据不能为空!");
        Assert.hasText(product.getProductid(), "产品ID不能为空!");
        productDao.updateProduct(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        productDao.deleteProduct(id);
    }

    @Transactional
    public void updateExample(String processid) {

    }

    @Transactional
    public void getExample (String processid) {

    }
}
