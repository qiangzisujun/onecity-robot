package com.tangchao.shop.advice;

import com.tangchao.shop.annotation.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class DataSourceAspect implements Ordered {

    @Pointcut("@annotation(com.tangchao.shop.annotation.DataSource)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource ds = method.getAnnotation(DataSource.class);
        if (ds == null) {
            DynamicDataSource.setDataSource(DataSourceNames.FIRST);
            log.debug("set datasource is " + DataSourceNames.SECOND);
        } else {
            DynamicDataSource.setDataSource(ds.name());
            log.debug("set datasource is " + ds.name());
        }

        try {
            return point.proceed();
        } finally {
            DynamicDataSource.clearDataSource();
            log.debug("clean datasource");
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
