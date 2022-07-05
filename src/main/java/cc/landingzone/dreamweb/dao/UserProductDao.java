package cc.landingzone.dreamweb.dao;

import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.UserProduct;

/**
 * 操作用户-产品权限表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public interface UserProductDao {

    List<String> listProductId(String userName);

    List<Product> listProduct(String userName);

    List<UserProduct> listUserProduct(Map<String, Object> map);

    Integer getUserProductTotal(Map<String, Object> map);

    void saveUserProduct(UserProduct userProduct);

    void updateUserProduct(UserProduct userProduct);

    void deleteUserProduct(Integer id);

    UserProduct getUserProduct(String productId, String userName);

    UserProduct getUserProductById(Integer id);
}
