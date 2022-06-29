package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.Product;

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
}
