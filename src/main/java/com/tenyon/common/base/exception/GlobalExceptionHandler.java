package com.tenyon.common.base.exception;

import com.tenyon.common.base.response.RtnData;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public RtnData<?> businessExceptionHandler(BusinessException e) {
        logger.error("BusinessException", e);
        return RtnData.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public RtnData<?> runtimeExceptionHandler(RuntimeException e) {
        logger.error("RuntimeException", e);
        return RtnData.fail(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public RtnData<?> handleException(Exception e) {
        return RtnData.fail(ErrorCode.SYSTEM_ERROR);
    }
}
