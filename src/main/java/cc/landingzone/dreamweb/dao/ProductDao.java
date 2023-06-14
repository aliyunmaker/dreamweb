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
 */
@Component
public interface ProductDao {

    List<Product> listProduct(Map<String, Object> map);

    Integer getProductTotal(Map<String, Object> map);

    Product getProductByServicecatalogProductId(String servicecatalogProductId);

    void updateProduct(Product product);

    Product getProductById(Integer id);

    void deleteProduct(Integer id);

    void saveProduct(Product product);

    Product getProductByProductName(String productName);

}
