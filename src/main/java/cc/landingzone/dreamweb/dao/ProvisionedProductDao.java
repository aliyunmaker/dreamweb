package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.ProvisionedProduct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 操作数据库产品实例表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public interface ProvisionedProductDao {

    Integer getExampleId(String exampleName);

    List<ProvisionedProduct> listExample(Map<String, Object> map);

    void saveExample(ProvisionedProduct provisionedProduct);

    Integer getExampleTotal(Map<String, Object> map);

    List<String> listExampleId();

    String getUserName(String exampleId);

    Integer getRoleId(String exampleId);

    String getProductIdByExampleId(String exampleId);

    void updateStatus(String status, String exampleId);

    void updateParameter(String parameter, String exampleId);

    void updateOutputs(String outputs, String exampleId);

}
