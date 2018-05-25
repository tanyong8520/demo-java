package com.tany.demo.service.model;

import java.io.Serializable;

public class SysPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long pid;
    private Integer available;
    private String name;
    private String url;


    public Long getId() {
        return pid;
    }

    public void setId(Long pid) {
        this.pid = pid;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SysPermission{" +
                "id=" + pid +
                ", available=" + available +
                ", name=" + name +
                ", url=" + url +
                "}";
    }
}
