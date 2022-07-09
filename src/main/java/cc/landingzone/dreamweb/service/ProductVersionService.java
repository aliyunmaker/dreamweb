package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductVersionDao;
import cc.landingzone.dreamweb.model.Page;
import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.ProductVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品版本service层
 * @author: laodou
 * @createDate: 2022/7/8
 */
@Component
public class ProductVersionService {

    @Autowired
    private ProductVersionDao productVersionDao;

    @Transactional
    public List<String> listApplication (String productId) {
        return productVersionDao.listApplication(productId);
    }

    @Transactional
    public List<String> listScenes (String productId, String application) {
        return productVersionDao.listScenes(productId, application);
    }

    @Transactional
    public String getProductVersionId (String productId, String application, String scene) {
        return productVersionDao.getProductVersionId(productId, application, scene);
    }

    @Transactional
    public List<ProductVersion> listProductVersion(Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        List<ProductVersion> list = productVersionDao.listProductVersion(map);
        if (null != page) {
            if (null != page.getStart() && null != page.getLimit()) {
                Integer total = productVersionDao.getProductVersionTotal(map);
                page.setTotal(total);
            } else {
                page.setTotal(list.size());
            }
        }
        return list;
    }

    @Transactional
    public void saveProductVersion(Product product) {
        String productVersion = getProductVersionId(product.getProductId(), product.getApplication(), product.getScenes());
        if (productVersion != null) {
            throw new IllegalArgumentException("此产品版本(" + product.getProductVersionId()+ ")已存在");
        }
        productVersionDao.saveProductVersion(product);
    }

    @Transactional
    public Product getProductVersionById(Integer id) {
        return productVersionDao.getProductVersionById(id);
    }

    @Transactional
    public void updateProductVersion(Product product) {
        Assert.notNull(product, "数据不能为空!");
        String productVersion = getProductVersionId(product.getProductId(), product.getApplication(), product.getScenes());
        if (productVersion != null) {
            throw new IllegalArgumentException("此产品版本(" + product.getProductVersionId()+ ")已存在");
        }
        productVersionDao.updateProductVersion(product);
    }

    @Transactional
    public void deleteProductVersion(Integer id) {
        productVersionDao.deleteProductVersion(id);
    }
}
