package com.tany.demo.quzrtz;

public class ScheduleJobModel {

    public static final String JOB_MODEL_KEY = "JOB_MODEL_KEY";

    private Long taskId;
    private Long createUserid;
    private String taskName;
    private String taskGroupName;
    private String inputFields;
    private String scriptName;
    private Integer calcType;
    private Integer status;  //0：运行，1：删除，2：暂停，3：异常
    private String cornString;
    private String createTime;
    private String updateTime;
    private String taskDescribe;
    private String nickName;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getCreateUserid() {
        return createUserid;
    }

    public void setCreateUserid(Long createUserid) {
        this.createUserid = createUserid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskGroupName() {
        return taskGroupName;
    }

    public void setTaskGroupName(String taskGroupName) {
        this.taskGroupName = taskGroupName;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCornString() {
        return cornString;
    }

    public void setCornString(String cornString) {
        this.cornString = cornString;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getTaskDescribe() {
        return taskDescribe;
    }

    public void setTaskDescribe(String taskDescribe) {
        this.taskDescribe = taskDescribe;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getInputFields() {
        return inputFields;
    }

    public void setInputFields(String inputFields) {
        this.inputFields = inputFields;
    }

    public Integer getCalcType() {
        return calcType;
    }

    public void setCalcType(Integer calcType) {
        this.calcType = calcType;
    }
}
