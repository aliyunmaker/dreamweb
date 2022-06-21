package cc.landingzone.dreamweb.dao;


import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.Product;

import java.util.List;
import java.util.Map;

/**
 * 操作产品表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public interface ProductDao {

    List<Product> searchProduct(Map<String, Object> map);

    List<String> getApplication();

    List<String> getScenes(String application);

    String getProductId(String application, String scene);

    String getProductName(String productId);

    Integer searchProductTotal(Map<String, Object> map);

    Product getProductByProductId(String productId);

    void addProduct(Product product);

    void updateProduct(Product product);

    Product getProductById(Integer id);

    void deleteProduct(Integer id);

}
