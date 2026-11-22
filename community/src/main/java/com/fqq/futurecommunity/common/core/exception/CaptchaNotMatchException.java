package com.fqq.futurecommunity.common.core.exception;

/**
* @author: fqq
* @Date: 2026/4/12 - 04 - 12 - 0:20 
* @Description: com.fqq.futurecommunity.framework.security
* @version: 1.0
 * 验证码异常
 * @author spikeCong
 * @date 2023/5/4
 **/
public class CaptchaNotMatchException extends CustomException{
//    public CaptchaNotMatchException(String defaultMessage) {//BaseException
//        super(defaultMessage);
//    }
    public CaptchaNotMatchException(){
        super(400,"验证码错误");
    }
}
