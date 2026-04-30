package com.fqq.futurecommunity.framework.security;

/**
* @author: fqq
* @Date: 2026/4/12 - 04 - 12 - 0:20 
* @Description: com.fqq.futurecommunity.framework.security
* @version: 1.0
*/

import com.fqq.futurecommunity.common.core.exception.BaseException;
import com.fqq.futurecommunity.common.core.exception.CustomException;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:43
 * @Description: com.fqq.futurecommunity.common.core.exception
 * @version: 1.0

 * 验证码异常
 * @author spikeCong
 * @date 2023/5/4
 **/
public class CaptchaNotMatchException extends BaseException {
    public CaptchaNotMatchException(String defaultMessage) {
        super(defaultMessage);
    }
}
