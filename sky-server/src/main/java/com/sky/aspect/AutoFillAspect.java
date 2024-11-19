package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.constant.CustomConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import com.sky.exception.AnnotationIncorrectUseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
 *  公共字段自动填充切面
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /*
     * 切入点
     */
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /*
     * 前置通知，为公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws AnnotationIncorrectUseException {
        log.info("开始公共字段自动填充...");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            throw new AnnotationIncorrectUseException(CustomConstant.ARGS_MISSING_ERROR);
        }

        Object entity = args[0];

        Long id = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        try {
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, id);
            if (operationType == OperationType.INSERT) {
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, id);
            }
        } catch (NoSuchMethodException e) {
            throw new AnnotationIncorrectUseException(CustomConstant.FIELD_NOT_EXIST_ERROR);
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.error(CustomConstant.AUTO_FILL_FAILED_ERROR);
        }
    }
}
