package cc.landingzone.dreamweb.dao;

import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.UserProductAssociate;

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

    List<UserProductAssociate> listUserProduct(Map<String, Object> map);

    Integer getUserProductTotal(Map<String, Object> map);

    void saveUserProduct(UserProductAssociate userProductAssociate);

    void updateUserProduct(UserProductAssociate userProductAssociate);

    void deleteUserProduct(Integer id);

    UserProductAssociate getUserProduct(String productId, String userName);

    UserProductAssociate getUserProductById(Integer id);

    String getPortfolioId(String productId, String userName);

}
