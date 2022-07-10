package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.ProductVersion;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ProductVersionDao {

    List<String> listApplication(String productId);

    List<String> listScenes(String productId, String application);

    String getProductVersionId(String productId, String application, String scene);

    List<ProductVersion> listProductVersion(Map<String, Object> map); //1

    Integer getProductVersionTotal(Map<String, Object> map); //1

    ProductVersion getProductVersionById(Integer id);

    void saveProductVersion(ProductVersion productVersion);

    void updateProductVersion(ProductVersion productVersion);

    void deleteProductVersion(Integer id);

    ProductVersion getProductVersionByServicecatalogProductVersionId(String servicecatalogProductVersionId);

}
