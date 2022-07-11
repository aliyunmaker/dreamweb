package cc.landingzone.dreamweb.model;

/**
 * 获取登录用户待办任务列表时使用（相比Application多了当前任务相关信息）
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public class Assignment implements Comparable<Assignment>{
    private String starterName;

    private String processTime;

    private String taskTime;

    private String taskId;

    private String taskName;

    private String processId;

    private String assignee;

    private String servicecatalogPlanId;

    @Override
    public int compareTo(Assignment assignment) {
        return Integer.parseInt(assignment.getProcessId()) - Integer.parseInt(this.getProcessId());
    }

    public String getServicecatalogPlanId() {
        return servicecatalogPlanId;
    }

    public void setServicecatalogPlanId(String servicecatalogPlanId) {
        this.servicecatalogPlanId = servicecatalogPlanId;
    }

    public String getStarterName() {
        return starterName;
    }

    public void setStarterName(String starterName) {
        this.starterName = starterName;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
