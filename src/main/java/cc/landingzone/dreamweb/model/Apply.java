package cc.landingzone.dreamweb.model;

/**
 * 工作流实例创建时存储基本信息
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public class Apply {

    private String startername;

    private String processtime;

    private String processid;

    private String task;

    private String processstate;

    private String parameters;

    private String cond;

    private String processdefinitionid;

    private String opinion;

    private String region;

    private String versionid;

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getVersionid() {
        return versionid;
    }

    public void setVersionid(String versionid) {
        this.versionid = versionid;
    }

    public String getProcessdefinitionid() {
        return processdefinitionid;
    }

    public void setProcessdefinitionid(String processdefinitionid) {
        this.processdefinitionid = processdefinitionid;
    }

    public String getStartername() {
        return startername;
    }

    public void setStartername(String startername) {
        this.startername = startername;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getProcesstime() {
        return processtime;
    }

    public void setProcesstime(String processtime) {
        this.processtime = processtime;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getProcessstate() {
        return processstate;
    }

    public void setProcessstate(String processstate) {
        this.processstate = processstate;
    }

}
