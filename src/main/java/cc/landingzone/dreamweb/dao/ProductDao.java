package cc.landingzone.dreamweb.dao;


import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.Product;

import java.util.List;
import java.util.Map;

@Component
public interface ProductDao {

    List<Product> searchProduct(Map<String, Object> map);

    List<String> getApplication();

    List<String> getScenes(String application);

    String getProductId(String application, String scene);

    Integer getExampleId(String productId, String exampleName);

    void addExample(String productId, String exampleName);

    Integer searchProductTotal(Map<String, Object> map);

    Product getProductByProductId(String productId);

    void addProduct(Product product);

    void updateProduct(Product product);

    Product getProductById(Integer id);

    void deleteProduct(Integer id);
}
