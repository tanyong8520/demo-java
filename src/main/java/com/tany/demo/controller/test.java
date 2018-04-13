package com.tany.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tany")
public class test {
    @RequestMapping("/test")
    public String list(){
        //查询列表数据
        return "test";
    }
}
