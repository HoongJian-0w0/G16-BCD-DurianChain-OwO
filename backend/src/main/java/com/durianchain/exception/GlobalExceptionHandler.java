package com.durianchain.exception;

import com.durianchain.common.result.Result;
import com.durianchain.common.result.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result handleServiceException(ServiceException ex) {
        return Result.error()
                .code(ex.getCode())
                .message(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception ex) {
        return Result.error()
                .code(ResultCode.INTERNAL_SERVER_ERROR)
                .message("Internal Server Error: " + ex.getMessage());
    }
}
