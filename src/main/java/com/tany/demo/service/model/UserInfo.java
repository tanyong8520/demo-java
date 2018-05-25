package com.tany.demo.service.model;

import java.io.Serializable;
import java.util.List;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long uid;
    private String name;
    private String password;
    private String salt;
    private Integer state;
    private String username;

    private List<SysRole> sysRoleList;


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<SysRole> getSysRoleList() {
        return sysRoleList;
    }

    public void setSysRoleList(List<SysRole> sysRoleList) {
        this.sysRoleList = sysRoleList;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid=" + uid +
                ", name=" + name +
                ", password=" + password +
                ", salt=" + salt +
                ", state=" + state +
                ", username=" + username +
                "}";
    }

    /**
     * 密码盐.
     *
     * @return
     */
    public String getCredentialsSalt() {
        return this.username + this.salt;
    }
}
