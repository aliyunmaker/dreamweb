package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;
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

    @Autowired
    private ProductDao productDao;

//    @Transactional
//    public String getProductName (String productId) {
//        return productDao.getProductName(productId);
//    }


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
        Product product1 = productDao.getProductByServicecatalogProductId(product.getServicecatalogProductId());
        if(product1 != null) {
            throw new IllegalArgumentException("此产品(" + product.getServicecatalogProductId()+ ")已存在");
        }
        productDao.saveProduct(product);
    }

    @Transactional
    public Product getProductById(Integer id) {
        return productDao.getProductById(id);
    }

    @Transactional
    public Product getProductByServicecatalogProductId (String servicecatalogProductId) {
        return productDao.getProductByServicecatalogProductId(servicecatalogProductId);
    }

    @Transactional
    public void updateProduct(Product product) {
        Assert.notNull(product, "数据不能为空!");
        Assert.hasText(product.getServicecatalogProductId(), "产品ID不能为空!");
        Product product1 = productDao.getProductByServicecatalogProductId(product.getServicecatalogProductId());
        if(product1 != null && product1.getId() != product.getId()) {
            throw new IllegalArgumentException("此产品(" + product.getServicecatalogProductId()+ ")已存在");
        }
        productDao.updateProduct(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        productDao.deleteProduct(id);
    }

}
