package com.fqq.futurecommunity.common.core.exception;

import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public BaseResponse<?> baseExceptionHandler(BaseException e) {
        return BaseResponse.fail(e.getDefaultMessage());
    }

    @ExceptionHandler(CustomException.class)
    public BaseResponse<?> businessException(CustomException e) {
        if (Objects.isNull(e.getCode())) {
            return BaseResponse.fail(e.getMsg());
        }
        return BaseResponse.fail(String.valueOf(e.getCode()), e.getMsg(), e.isSuccess());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public BaseResponse<?> illegalArgumentException(IllegalArgumentException e) {
        return BaseResponse.fail("400", e.getMessage());
    }
}
