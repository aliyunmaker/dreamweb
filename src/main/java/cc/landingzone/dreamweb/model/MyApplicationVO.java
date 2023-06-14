package cc.landingzone.dreamweb.model;

/**
 * @description:
 * @author: laodou
 * @createDate: 2022/7/11
 */
public class MyApplicationVO {
    private Integer id;

    private String starterName;

    private String createTime;

    private String processId;

    private String processState;

    private String cond;

    private String opinion;

    private String servicecatalogPlanId;

    private String planResult;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStarterName() {
        return starterName;
    }

    public void setStarterName(String starterName) {
        this.starterName = starterName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessState() {
        return processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getServicecatalogPlanId() {
        return servicecatalogPlanId;
    }

    public void setServicecatalogPlanId(String servicecatalogPlanId) {
        this.servicecatalogPlanId = servicecatalogPlanId;
    }

    public String getPlanResult() {
        return planResult;
    }

    public void setPlanResult(String planResult) {
        this.planResult = planResult;
    }
}
