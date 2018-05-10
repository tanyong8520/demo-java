package com.tany.demo.optLog;

import com.tany.demo.Utils.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OptLogAspect {
    private static Logger logger = LoggerFactory.getLogger(OptLogAspect.class);

    @Pointcut("@annotation(com.tany.demo.optLog.OptLog)")
    public void logPointCut() {
        System.out.println("optlog cut!");
    }

    @AfterReturning(pointcut="logPointCut()",returning="returnValue")
    public void saveOptLogAfter(JoinPoint joinPoint, Object returnValue) {
        try{
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取方法名称
            String methodName = signature.getName();
            System.out.println("optlog get function name:"+methodName);
            //获取参数
            Object[] args = joinPoint.getArgs();
            if(args.length>0 && args[0]!=null){
                System.out.println("optlog get function args:"+args[0].toString());
                if(returnValue instanceof ResultModel){
                    ResultModel resultModel = (ResultModel) returnValue;
                    System.out.println("optlog get function result:"+resultModel.getCode().toString());
                }
            }
        }catch (Exception e)
        {
            logger.error("saveOptLogAfter is error:{}", e.getMessage());
        }
    }
}
