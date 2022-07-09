package cc.landingzone.dreamweb.model;

/**
 * @description:
 * @author: laodou
 * @createDate: 2022/7/8
 */
public class UserProductAssociateVO {

    private Integer id;

    private String servicecatalogProductId;

    private String productName;

    private String loginName;

    private String servicecatalogPortfolioId;

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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getServicecatalogPortfolioId() {
        return servicecatalogPortfolioId;
    }

    public void setServicecatalogPortfolioId(String servicecatalogPortfolioId) {
        this.servicecatalogPortfolioId = servicecatalogPortfolioId;
    }
}
