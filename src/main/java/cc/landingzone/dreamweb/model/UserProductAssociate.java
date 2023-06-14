package cc.landingzone.dreamweb.model;

public class UserProductAssociate {

    private Integer id;

    private Integer productId;

    private Integer userId;

    private String servicecatalogPortfolioId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getServicecatalogPortfolioId() {
        return servicecatalogPortfolioId;
    }

    public void setServicecatalogPortfolioId(String servicecatalogPortfolioId) {
        this.servicecatalogPortfolioId = servicecatalogPortfolioId;
    }
}
