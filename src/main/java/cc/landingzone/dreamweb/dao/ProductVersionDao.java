package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ProductVersionDao {

    List<String> listApplication(String productId);

    List<String> listScenes(String productId, String application);

    String getProductVersionId(String productId, String application, String scene);

    List<Product> listProductVersion(Map<String, Object> map);

    Integer getProductVersionTotal(Map<String, Object> map);

    Product getProductVersionById(Integer id);

    void saveProductVersion(Product product);

    void updateProductVersion(Product product);

    void deleteProductVersion(Integer id);

}
