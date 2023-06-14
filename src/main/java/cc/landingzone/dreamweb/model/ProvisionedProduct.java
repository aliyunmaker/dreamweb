package cc.landingzone.dreamweb.model;

/**
 * 产品实例
 *
 * @author: laodou
 * @createDate: 2022/6/21
 */
public class ProvisionedProduct {

    private Integer id;

    private String servicecatalogProvisionedProductId;

    private String provisionedProductName;

    private Integer productId;

    private Integer roleId;

    private Integer starterId;

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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getStarterId() {
        return starterId;
    }

    public void setStarterId(Integer starterId) {
        this.starterId = starterId;
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
