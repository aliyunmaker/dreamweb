package cc.landingzone.dreamweb.dao;

import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.model.Product;
import cc.landingzone.dreamweb.model.UserProductAssociate;
import org.springframework.stereotype.Component;

/**
 * 操作用户-产品权限表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public interface UserProductDao {

    List<UserProductAssociate> listUserProductAssociate(Map<String, Object> map);

    Integer getUserProductAssociateTotal(Map<String, Object> map);

    void saveUserProductAssociate(UserProductAssociate userProductAssociate);

    void updateUserProductAssociate(UserProductAssociate userProductAssociate);

    void deleteUserProductAssociate(Integer id);

    UserProductAssociate getUserProductAssociateByProductIdAndUserId(Integer productId, Integer userId);

    UserProductAssociate getUserProductAssociateById(Integer id);

    String getServicecatalogPortfolioId(Integer productId, Integer userId);

    List<Integer> listProductIdsByUserId(Integer userId);

}
