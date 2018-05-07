package com.tany.demo.service.model;

import java.util.List;

public class SysRole {
    private static final long serialVersionUID = 1L;

    private Long rid;
    private Integer available;
    private String description;
    private String role;

    private List<SysPermission> sysPermissionList;

    public Long getId() {
        return rid;
    }

    public void setId(Long rid) {
        this.rid = rid;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<SysPermission> getSysPermissionList() {
        return sysPermissionList;
    }

    public void setSysPermissionList(List<SysPermission> sysPermissionList) {
        this.sysPermissionList = sysPermissionList;
    }

    @Override
    public String toString() {
        return "SysRole{" +
                "id=" + rid +
                ", available=" + available +
                ", description=" + description +
                ", role=" + role +
                "}";
    }
}
