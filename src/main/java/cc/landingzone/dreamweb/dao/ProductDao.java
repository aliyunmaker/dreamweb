package cc.landingzone.dreamweb.dao;


import org.springframework.stereotype.Component;

import cc.landingzone.dreamweb.model.Product;

import java.util.List;
import java.util.Map;

/**
 * 操作数据库产品表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public interface ProductDao {

    List<Product> listProductVersion(Map<String, Object> map);

    Integer getProductVersionTotal(Map<String, Object> map);

    List<Product> listProduct(Map<String, Object> map);

    Integer getProductTotal(Map<String, Object> map);

//    List<Product> listProductDistinct(Map<String, Object> map);

    List<String> listApplication(String productId);

    String getPortfolioId(String productId, String userName);

    List<String> listScenes(String productId, String application);

    String getProductVersionId(String productId, String application, String scene);

    String getProductName(String productId);

//    Integer getProductDistinctTotal(Map<String, Object> map);

    Product getProductByProductId(String productId);

    void saveProduct(Product product);

    void updateProduct(Product product);

    Product getProductVersionById(Integer id);

    Product getProductById(Integer id);

    void deleteProduct(Integer id);

}
