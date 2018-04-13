package com.tany.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan(basePackages={"com.tany.demo.mapper.*"})
public class ServerConfig {

}
