package cc.landingzone.dreamweb.model;

/**
 * @description:
 * @author: laodou
 * @createDate: 2022/7/8
 */
public class ProductVersion {
    private Integer id;

    private String servicecatalogProductVersionId;

    private Integer productId;

    private String app;

    private String environment;

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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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
}
