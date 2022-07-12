package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ProductVersionDao;
import cc.landingzone.dreamweb.model.Page;
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
    public List<String> listApps(Integer productId) {
        return productVersionDao.listApps(productId);
    }

    @Transactional
    public List<String> listEnvironments (Integer productId, String app) {
        return productVersionDao.listEnvironments(productId, app);
    }

    @Transactional
    public String getServicecatalogProductVersionId (Integer productId, String app, String environment) {
        return productVersionDao.getServicecatalogProductVersionId(productId, app, environment);
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
    public void saveProductVersion(ProductVersion productVersion) {
        ProductVersion productVersion1 = productVersionDao.getProductVersionByServicecatalogProductVersionId(productVersion.getServicecatalogProductVersionId());
        if (productVersion1 != null) {
            throw new IllegalArgumentException("此产品版本(" + productVersion.getServicecatalogProductVersionId()+ ")已存在");
        }
        productVersionDao.saveProductVersion(productVersion);
    }

    @Transactional
    public ProductVersion getProductVersionById(Integer id) {
        return productVersionDao.getProductVersionById(id);
    }

    @Transactional
    public ProductVersion getProductVersionByServicecatalogProductVersionId (String servicecatalogProductVersionId) {
        return productVersionDao.getProductVersionByServicecatalogProductVersionId(servicecatalogProductVersionId);
    }

    @Transactional
    public void updateProductVersion(ProductVersion productVersion) {
        Assert.notNull(productVersion, "数据不能为空!");
        Assert.notNull(productVersion.getProductId(), "产品不能为空!");
        ProductVersion productVersion1 = productVersionDao.getProductVersionByServicecatalogProductVersionId(productVersion.getServicecatalogProductVersionId());
        if (productVersion1 != null && productVersion1.getId()!=productVersion.getId()) {
            throw new IllegalArgumentException("此产品版本(" + productVersion.getServicecatalogProductVersionId()+ ")已存在");
        }
        productVersionDao.updateProductVersion(productVersion);
    }

    @Transactional
    public void deleteProductVersion(Integer id) {
        productVersionDao.deleteProductVersion(id);
    }
}
