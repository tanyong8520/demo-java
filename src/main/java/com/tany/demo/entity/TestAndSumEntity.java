package com.tany.demo.entity;

import java.util.Date;
import java.util.List;

public class TestAndSumEntity extends TestEntity {
    private static final long serialVersionUID = 1L;

    //子信息列表
    private List<sumTableEntity> sumTableList;
    /**
     * 设置：子信息列表
     */
    public void setSumTableEntityList(List<sumTableEntity> sumTableList){
        this.sumTableList = sumTableList;
    }
    /**
     * 获取：子信息列表
     */
    public List<sumTableEntity> getSumTableEntityList(){
        return sumTableList;
    }
}
