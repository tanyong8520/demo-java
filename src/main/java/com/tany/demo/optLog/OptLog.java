package com.tany.demo.optLog;

import java.lang.annotation.*;

@Target(ElementType.METHOD)//描述使用范围（方法上）
@Retention(RetentionPolicy.RUNTIME)//描述使用周期
@Documented//说明该注解将被包含在javadoc中//可文档化
public @interface OptLog {
    String value() default "";
}
