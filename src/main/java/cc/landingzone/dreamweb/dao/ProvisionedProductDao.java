package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.ProvisionedProduct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 操作产品实例表
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public interface ProvisionedProductDao {

    Integer getExampleId(String exampleName);

    List<ProvisionedProduct> searchExample(Map<String, Object> map);

    void addExample(ProvisionedProduct provisionedProduct);

    Integer searchExampleTotal(Map<String, Object> map);

    List<String> searchExampleId();

    String getUserName(String exampleId);

    Integer getRoleId(String exampleId);

    void updateStatus(String status, String exampleId);

    void updateParameter(String parameter, String exampleId);

    void updateOutputs(String outputs, String exampleId);

}
