package com.tany.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {"application.properties"})
@ComponentScan(basePackages = {"com.tany.demo"})
public class TestApplicationConfig {
}
