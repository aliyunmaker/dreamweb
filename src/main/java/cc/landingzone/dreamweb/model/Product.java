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

    private String application;

    private String scenes;

    private String productId;

    private String productName;

    private String productVersionId;


    public String getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(String productVersionId) {
        this.productVersionId = productVersionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getScenes() {
        return scenes;
    }

    public void setScenes(String scenes) {
        this.scenes = scenes;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}