package cc.landingzone.dreamweb.model;

/**
 * 获取登录用户待办任务列表时使用（相比Apply多了当前任务相关信息）
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
public class Assignment {
    private String startername;

    private String processtime;

    private String tasktime;

    private String taskid;

    private String taskname;

    private String processid;

    private String assignee;

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getStartername() {
        return startername;
    }

    public void setStartername(String startername) {
        this.startername = startername;
    }

    public String getProcesstime() {
        return processtime;
    }

    public void setProcesstime(String processtime) {
        this.processtime = processtime;
    }

    public String getTasktime() {
        return tasktime;
    }

    public void setTasktime(String tasktime) {
        this.tasktime = tasktime;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
