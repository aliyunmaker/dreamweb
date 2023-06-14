package cc.landingzone.dreamweb.model;

/**
 * @description:
 * @author: laodou
 * @createDate: 2022/7/11
 */
public class ProvisionedProductVO {

    private Integer id;

    private String servicecatalogProvisionedProductId;

    private String provisionedProductName;

    private String servicecatalogProductId;

    private String productName;

    private Integer roleId;

    private String starterName;

    private String status;

    private String parameter;

    private String outputs;

    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServicecatalogProvisionedProductId() {
        return servicecatalogProvisionedProductId;
    }

    public void setServicecatalogProvisionedProductId(String servicecatalogProvisionedProductId) {
        this.servicecatalogProvisionedProductId = servicecatalogProvisionedProductId;
    }

    public String getProvisionedProductName() {
        return provisionedProductName;
    }

    public void setProvisionedProductName(String provisionedProductName) {
        this.provisionedProductName = provisionedProductName;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getStarterName() {
        return starterName;
    }

    public void setStarterName(String starterName) {
        this.starterName = starterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
