package com.tany.demo.entity;

import java.util.Date;

public class sumTableEntity {
    private static final long serialVersionUID = 1L;

    //主键
    private Long id;
    //业务ID
    private Long alertId;
    //创建时间
    private Date createTime;
    //数据
    private String data;
    //描述
    private String desc;
    //子信息列表


    /**
     * 设置：主键
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * 获取：主键
     */
    public Long getId() {
        return id;
    }
    /**
     * 设置：业务ID
     */
    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }
    /**
     * 获取：业务ID
     */
    public Long getAlertId() {
        return alertId;
    }
    /**
     * 设置：创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    /**
     * 获取：创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }
    /**
     * 设置：数据
     */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * 获取：数据
     */
    public String getData() {
        return data;
    }
    /**
     * 设置：描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    /**
     * 获取：描述
     */
    public String getDesc() {
        return desc;
    }
}
