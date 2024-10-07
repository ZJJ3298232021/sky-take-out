package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.constant.SqlExceptionConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL完整性约束违反异常的处理器方法
     * 当数据库操作违反了完整性约束（如唯一约束）时，此方法将被调用
     *
     * @param ex SQL完整性约束违反异常对象，包含违反约束的详细信息
     * @return 返回一个Result对象，包含处理结果或错误信息
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if (message.contains(SqlExceptionConstant.DUPLICATE_ENTRY)) {
            String[] strings = message.split(" ");
            return Result.error(strings[2] + MessageConstant.ALREADY_EXISTS);
        } else
            return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
