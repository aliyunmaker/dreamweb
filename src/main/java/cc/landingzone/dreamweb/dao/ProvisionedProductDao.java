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

    ProvisionedProduct getProvisionedProductByProvisionedProductName(String provisionedProductName);

    List<ProvisionedProduct> listProvisionedProducts(Map<String, Object> map);

    Integer getProvisionedProductTotal(Map<String, Object> map);


    void saveProvisionedProduct(ProvisionedProduct provisionedProduct);

    List<String> listServicecatalogProvisionedProductIdUnderChange();

    ProvisionedProduct getServicecatalogProvisionedProductByProvisionedProductId(String servicecatalogProvisionedProductId);

    void updateStatusByServicecatalogProvisionedProductId(String status, String servicecatalogProvisionedProductId);

    void updateParameterByServicecatalogProvisionedProductId(String parameter, String servicecatalogProvisionedProductId);

    void updateOutputsByServicecatalogProvisionedProductId(String outputs, String servicecatalogProvisionedProductId);

}
