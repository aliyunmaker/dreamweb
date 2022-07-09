package cc.landingzone.dreamweb.model;

/**
 * 产品信息
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public class Product {
    private Integer id;

    private String servicecatalogProductId;

    private String productName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServicecatalogProductId() {
        return servicecatalogProductId;
    }

    public void setServicecatalogProductId(String servicecatalogProductId) {
        this.servicecatalogProductId = servicecatalogProductId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}