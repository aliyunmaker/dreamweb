package cc.landingzone.dreamweb.dao;


import cc.landingzone.dreamweb.model.Provisioned_product;
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

    String getProductName(String productId);

    Integer getExampleId(String exampleName);

    void addExample(Provisioned_product provisioned_product);

    List<Provisioned_product> searchExample(Map<String, Object> map);

    Integer searchExampleTotal(Map<String, Object> map);

    Integer searchProductTotal(Map<String, Object> map);

    Product getProductByProductId(String productId);

    void addProduct(Product product);

    void updateProduct(Product product);

    Product getProductById(Integer id);

    void deleteProduct(Integer id);
}
