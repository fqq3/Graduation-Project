package com.fqq.community.common.core.exception;

import com.fqq.community.common.core.domain.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: fqq
 * @Date: 2026/3/7 - 03 - 07 - 18:09
 * @Description: com.fqq.community.common.core.exception
 * @version: 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /** * 基础异常 * @param e * @return: com.fqq.community.common.core.domain.BaseResponse */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public BaseResponse baseExceptionHandler(BaseException e){
        return BaseResponse.fail(e.getDefaultMessage());
    }
}
