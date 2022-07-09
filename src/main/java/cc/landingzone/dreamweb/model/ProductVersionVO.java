package cc.landingzone.dreamweb.model;

/**
 * @description:
 * @author: laodou
 * @createDate: 2022/7/8
 */
public class ProductVersionVO {
    private Integer id;

    private String servicecatalogProductVersionId;

    private String productName;

    private String app;

    private String environment;

    private String servicecatalogProductId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServicecatalogProductVersionId() {
        return servicecatalogProductVersionId;
    }

    public void setServicecatalogProductVersionId(String servicecatalogProductVersionId) {
        this.servicecatalogProductVersionId = servicecatalogProductVersionId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getServicecatalogProductId() {
        return servicecatalogProductId;
    }

    public void setServicecatalogProductId(String servicecatalogProductId) {
        this.servicecatalogProductId = servicecatalogProductId;
    }
}
