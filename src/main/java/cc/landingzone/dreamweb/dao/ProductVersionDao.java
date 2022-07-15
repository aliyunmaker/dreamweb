package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.ProductVersion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ProductVersionDao {

    List<String> listApps(Integer productId);

    List<String> listEnvironments(Integer productId, String app);

    String getServicecatalogProductVersionId(Integer productId, String app, String environment);

    List<ProductVersion> listProductVersion(Map<String, Object> map); //1

    Integer getProductVersionTotal(Map<String, Object> map); //1

    ProductVersion getProductVersionById(Integer id);

    void saveProductVersion(ProductVersion productVersion);

    void updateProductVersion(ProductVersion productVersion);

    void deleteProductVersion(Integer id);

    ProductVersion getProductVersionByServicecatalogProductVersionId(String servicecatalogProductVersionId);

    void deleteProductVersionByProductId(Integer productId);
}
