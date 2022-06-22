package cc.landingzone.dreamweb.dao;

import java.util.List;

/**
 * 操作用户-产品权限表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public interface UserProductDao {

    List<String> listProductId(String userName);
}
